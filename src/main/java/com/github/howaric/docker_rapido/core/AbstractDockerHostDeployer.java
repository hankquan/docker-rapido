package com.github.howaric.docker_rapido.core;

import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.github.dockerjava.api.model.Container;
import com.github.howaric.docker_rapido.docker.DockerProxy;
import com.github.howaric.docker_rapido.docker.DockerProxyFactory;
import com.github.howaric.docker_rapido.exceptions.ContainerStartingFailedException;
import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.Repository;
import com.github.howaric.docker_rapido.yaml_model.Service;

public abstract class AbstractDockerHostDeployer implements DockerHostDeployer {

    private static Logger logger = LoggerFactory.getLogger(AbstractDockerHostDeployer.class);

    protected DockerProxy dockerProxy;

    protected LinkedList<Container> current = new LinkedList<>();

    protected String serviceName;
    protected String deployType;
    protected String owner;
    protected String imageName;
    protected Node node;
    protected Service service;
    protected Repository repository;

    @Override
    public void deploy(Repository repository, String deployType, String owner, Node node, String serviceName, Service service,
            String imageName) {
        this.repository = repository;
        this.deployType = deployType;
        this.owner = owner;
        this.node = node;
        this.service = service;
        this.serviceName = serviceName;
        this.imageName = imageName;
        String dockerEndPoint = node.getDockerEndPoint();
        dockerProxy = DockerProxyFactory.getInstance(dockerEndPoint);
        findCurrentContainers();
        perform();
    }

    protected abstract void perform();

    protected abstract void findCurrentContainers();

    protected String generateContainerName() {
        return getContainerNamePrefix() + "." + CommonUtil.getTimeStamp();
    }

    protected String getContainerNamePrefix() {
        return serviceName + "." + deployType + "." + owner;
    }

    protected void removeCurrentContainer() {
        if (CommonUtil.hasElement(current)) {
            Container container = current.poll();
            dockerProxy.stopContainer(container.getId(), service.getDeploy().getStop_timeout());
            dockerProxy.removeContainer(container.getId());
            logger.info("Stop and remove old container: " + container.getNames()[0].substring(1));
        }
    }

    protected static final String healthUrlTemplate = "http://%s:%s/health";

    /**
     * Directly check with container exposed port
     * 
     * @param port
     * @return
     */
    protected boolean isServiceOnPortReady(String port) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(healthUrlTemplate, node.getIp(), port);
        logger.info("Check url: {}", url);
        boolean isReady = false;
        for (int i = 0; i < 20; i++) {
            try {
                @SuppressWarnings("rawtypes")
                ResponseEntity<Map> result = restTemplate.getForEntity(url, Map.class);
                if (result.getStatusCode() == HttpStatus.OK) {
                    logger.info("Query result: {}", result.getBody());
                    String status = (String) result.getBody().get("status");
                    if ("UP".equals(status)) {
                        logger.info("Service on port {} is ready!", port);
                        isReady = true;
                        break;
                    }
                } else {
                    logger.warn(result.getStatusCode() + ": " + result.getBody());
                }
            } catch (Exception e) {
                logger.warn("Not ready, continue to check...");
                CommonUtil.sleep(5000);
            }
        }
        logger.info("Check result: " + isReady);
        if (isReady) {
            return isReady;
        } else {
            throw new ContainerStartingFailedException("Service on port " + port + " is not ready!");
        }
    }

}
