package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.yaml_model.Healthcheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.google.common.base.Strings;

public class RollingUpdateDeployerProcessor extends DeployProcessor {

    private static Logger logger = LoggerFactory.getLogger(RollingUpdateDeployerProcessor.class);

    @Override
    protected void perform() {
        Integer replicas = service.getDeploy().getReplicas();
        dockerProxy.pullImage(imageName, repository.getUsername(), repository.getPassword());
        for (int i = 1; i <= replicas; i++) {
            String containerId = dockerProxy.createContainer(generateContainerName(), imageName, service);
            dockerProxy.startContainer(containerId);

            if (!Strings.isNullOrEmpty(service.getBuild())) {
                if (!healthcheck.isDisable() && isContainerRegisteredSuccessfullyInConsul(containerId)) {
                    removeCurrentContainer();
                } else {
                    checkContainerStatus(containerId);
                }
            } else {
                checkContainerStatus(containerId);
                removeCurrentContainer();
            }
        }
        if (!Strings.isNullOrEmpty(service.getPublish_port())) {
            logger.info("Start to check service {}", serviceName);
            CommonUtil.sleep(5000);
            checkSpringActuatorHealthStatus(node.getIp(), service.getPublish_port());
        }
        while (CommonUtil.hasElement(current)) {
            removeCurrentContainer();
        }
    }

}
