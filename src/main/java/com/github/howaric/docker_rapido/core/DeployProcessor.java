package com.github.howaric.docker_rapido.core;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.howaric.docker_rapido.exceptions.ContainerStartingFailedException;
import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.github.howaric.docker_rapido.utils.LogUtil;
import com.github.howaric.docker_rapido.utils.PollExecutor;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public abstract class DeployProcessor extends AbstractNodeProcessor {

    private static Logger logger = LoggerFactory.getLogger(DeployProcessor.class);

    protected static final String baseUrl = "http://%s:%s/health";

    protected void checkServiceHealthStatus(String ip, String port, String uri) {
        String urlTemplate = baseUrl;
        if (!Strings.isNullOrEmpty(uri)) {
            if (!uri.startsWith("/")) {
                uri += "/";
            }
            urlTemplate += uri;
        } else {
            urlTemplate += "/health";
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(urlTemplate, ip, port);
        logger.info("Start to check service from url: {}", url);

        Boolean isReady = PollExecutor.poll(5, 120, () -> {
            ResponseEntity<Map> result = restTemplate.getForEntity(url, Map.class);
            if (result.getStatusCode() == HttpStatus.OK) {
                logger.info("Query result: {}", result.getBody());
                String status = (String) result.getBody().get("status");
                if ("UP".equals(status)) {
                    logger.info("Service on port {} is ready!", port);
                    return Boolean.TRUE;
                }
                logger.warn("Service {} not ready, continue to check in 5s...", serviceName);
            } else {
                logger.warn(result.getStatusCode() + ": " + result.getBody());
            }
            return null;
        });

        if (!isReady) {
            throw new ContainerStartingFailedException("Service on port " + port + " is not ready!");
        }
    }

    protected static final String consulCheckUrlTemplate = "http://%s:8500/v1/health/checks/%s";

    protected boolean isContainerRegisteredSuccessfullyInConsul(String containerId) {
        InspectContainerResponse inspectContainer = dockerProxy.inspectContainer(containerId);
        ContainerNetwork containerNetwork = inspectContainer.getNetworkSettings().getNetworks().get("bridge");
        if (containerNetwork != null) {
            String containerIp = containerNetwork.getIpAddress();
            logger.info("Get container IP: {}", containerIp);
            checkIfContainerRegisteredSuccessfullyInConsul(containerIp);
            return true;
        } else {
            throw new ContainerStartingFailedException("Get container Ip from network 'bridge' failed");
        }
    }

    protected void checkIfContainerRegisteredSuccessfullyInConsul(String clientIp) {
        String url = String.format(consulCheckUrlTemplate, node.getIp(), serviceName);
        logger.info("Start to check if application has been successfully registered in consul...");
        logger.info("Check url: {}", url);
        RestTemplate restTemplate = new RestTemplate();

        Boolean isReady = PollExecutor.poll(5, 120, () -> {
            ResponseEntity<List> result = restTemplate.getForEntity(url, List.class);
            if (result.getStatusCode() == HttpStatus.OK) {
                List<Map<String, String>> checkList = result.getBody();
                for (Map<String, String> map : checkList) {
                    String output = map.get("Output");
                    if (output.contains(clientIp) && output.contains("UP")) {
                        logger.info("Consul result: {}", output);
                        logger.info("Container is ready!");
                        return Boolean.TRUE;
                    }
                }
                logger.warn("Container {} not ready, continue to check in 5s...", clientIp);
            } else {
                logger.warn(result.getStatusCode() + ": " + result.getBody());
            }
            return null;
        });

        if (!isReady) {
            throw new ContainerStartingFailedException("Container is not healthy, starting failed");
        }
    }

    private static final int oneMinute = 60 * 1000;

    protected void checkContainerStatusAfterOneMinute(String containerId) {
        logger.info("Check container status after one minute");
        CommonUtil.sleep(oneMinute);
        String status = dockerProxy.inspectContainer(containerId).getState().getStatus();
        if (!isContainerUp(status)) {
            printContainerStartingLogs(containerId);
            throw new ContainerStartingFailedException(
                    "Container of service " + serviceName + " on node " + node.getIp() + " is down: " + containerId);
        }
        logger.info("Check passed");
    }

    protected boolean isContainerUp(String status) {
        if ("running".equals(status) || "healthy".equals(status)) {
            return true;
        }
        return false;
    }

    protected void printContainerStartingLogs(String containerId) {
        LogUtil.printEmptyLine();
        LogUtil.printInCentreWithStar("LOGS OF CONTAINER " + containerId);
        System.out.println("");
        dockerProxy.printLogs(containerId);
        System.out.println("");
        LogUtil.printInCentreWithStar("LOGS END");
        LogUtil.printEmptyLine();
    }
}
