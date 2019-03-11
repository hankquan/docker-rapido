package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForceUpdateDeployerProcessor extends DeployProcessor {

    private static Logger logger = LoggerFactory.getLogger(ForceUpdateDeployerProcessor.class);

    @Override
    protected void perform() {
        logger.info("Delete all existed containers");
        while (CommonUtil.hasElement(current)) {
            removeCurrentContainer();
        }

        Integer replicas = service.getDeploy().getReplicas();
        dockerProxy.pullImage(imageName, repository.getUsername(), repository.getPassword());

        for (int i = 1; i <= replicas; i++) {

            String containerId = dockerProxy.createContainer(generateContainerName(), imageName, service);
            dockerProxy.startContainer(containerId);

            if (!service.getDeploy().getHealthcheck().isDisable()) {
                isContainerRegisteredSuccessfullyInConsul(containerId);
            } else {
                checkContainerStatusAfterOneMinute(containerId);
            }

        }

        if (!service.getDeploy().getServicecheck().isDisable() && !Strings.isNullOrEmpty(service.getPublish_port())) {
            logger.info("Start to check service {}", serviceName);
            String uri = service.getDeploy().getServicecheck().getUri();
            checkServiceHealthStatus(node.getIp(), service.getPublish_port(), uri);
        }

    }

}
