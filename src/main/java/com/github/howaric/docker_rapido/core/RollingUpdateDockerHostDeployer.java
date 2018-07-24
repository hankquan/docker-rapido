package com.github.howaric.docker_rapido.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.howaric.docker_rapido.exceptions.ContainerStartingFailedException;
import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.google.common.base.Strings;

public class RollingUpdateDockerHostDeployer extends AbstractDockerHostDeployer {

    private static Logger logger = LoggerFactory.getLogger(RollingUpdateDockerHostDeployer.class);

    @Override
    protected void perform() {
        Integer replicas = service.getDeploy().getReplicas();
        for (int i = 1; i <= replicas; i++) {
            List<String> ports = service.getPorts();
            List<String> environment = service.getEnvironment();
            if (!dockerProxy.isImageExits(imageName)) {
                dockerProxy.pullImage(imageName, "", "");
            }
            String containerId = dockerProxy.createContainer(generateContainerName(), imageName, ports, environment, service.getLinks(),
                    service.getVolumes(), service.getExtra_hosts());
            dockerProxy.startContainer(containerId);

            InspectContainerResponse inspectContainer = dockerProxy.inspectContainer(containerId);
            ContainerNetwork containerNetwork = inspectContainer.getNetworkSettings().getNetworks().get("bridge");
            if (containerNetwork != null) {
                String containerIp = containerNetwork.getIpAddress();
                logger.info("Get container IP: {}", containerIp);
                if (isContainerUp(containerIp)) {
                    removeCurrentContainer();
                }
            } else {
                logger.warn("Get container Ip failed, container might not be up");
            }
        }
        if (!Strings.isNullOrEmpty(service.getPublish_port())) {
            logger.info("Start to check service {}", serviceName);
            checkService();
        }
        while (CommonUtil.hasElement(current)) {
            removeCurrentContainer();
        }
    }

    private static final String consulCheckUrlTemplate = "http://%s:8500/v1/health/checks/%s";

    private boolean checkService() {
        CommonUtil.sleep(5000);
        return isServiceOnPortReady(service.getPublish_port());
    }

    private boolean isContainerUp(String containerIp) {
        String url = String.format(consulCheckUrlTemplate, node.getIp(), serviceName);
        logger.info("Check url: {}", url);
        RestTemplate restTemplate = new RestTemplate();
        boolean isReady = false;
        outer: for (int i = 0; i < 20; i++) {
            try {
                @SuppressWarnings("rawtypes")
                ResponseEntity<List> result = restTemplate.getForEntity(url, List.class);
                if (result.getStatusCode() == HttpStatus.OK) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> checkList = result.getBody();
                    for (Map<String, String> map : checkList) {
                        String output = map.get("Output");
                        if (output.contains(containerIp) && output.contains("UP")) {
                            logger.info("output: {}", output);
                            logger.info("Container is ready!");
                            isReady = true;
                            break outer;
                        }
                    }
                    logger.warn("Not ready, continue to check...");
                } else {
                    logger.warn(result.getStatusCode() + ": " + result.getBody());
                }
            } catch (Exception e) {
                logger.warn("Contact with consul failed, continue to check...");
            }
            CommonUtil.sleep(5000);
        }
        if (isReady) {
            return isReady;
        } else {
            throw new ContainerStartingFailedException("Container is not healthy, starting failed");
        }
    }

    protected void findCurrentContainers() {
        List<Container> allRunningContainers = dockerProxy.listContainers(false);
        List<String> containerNames = new ArrayList<>();
        for (Container container : allRunningContainers) {
            String[] names = container.getNames();
            for (String name : names) {
                String realName = name.substring(1);
                if (!realName.contains("/") && realName.startsWith(getContainerNamePrefix())) {
                    current.add(container);
                    containerNames.add(realName);
                }
            }
        }
        logger.info("Find current containers: " + containerNames);
    }

}
