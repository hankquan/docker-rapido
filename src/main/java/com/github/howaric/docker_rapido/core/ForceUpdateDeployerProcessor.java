package com.github.howaric.docker_rapido.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.google.common.base.Strings;

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

            if (!Strings.isNullOrEmpty(service.getBuild()) && !healthcheck.isDisable()) {
                isContainerRegisteredSuccessfullyInConsul(containerId);
            }
            checkContainerStatus(containerId);
        }
        if (!Strings.isNullOrEmpty(service.getPublish_port())) {
            logger.info("Start to check service {}", serviceName);
            CommonUtil.sleep(5000);
            checkSpringActuatorHealthStatus(node.getIp(), service.getPublish_port());
        }

    }

}
