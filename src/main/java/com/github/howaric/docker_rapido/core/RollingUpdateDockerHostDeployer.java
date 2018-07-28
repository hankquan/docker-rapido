package com.github.howaric.docker_rapido.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.google.common.base.Strings;

public class RollingUpdateDockerHostDeployer extends AbstractDockerHostDeployer {

    private static Logger logger = LoggerFactory.getLogger(RollingUpdateDockerHostDeployer.class);

    @Override
    protected void perform() {
        Integer replicas = service.getDeploy().getReplicas();
        for (int i = 1; i <= replicas; i++) {

            dockerProxy.pullImage(imageName, repository.getUsername(), repository.getPassword());
            String containerId = dockerProxy.createContainer(generateContainerName(), imageName,
                    service.getDeploy().getRestart_policy().getCondition(), service.getPorts(), service.getEnvironment(),
                    service.getLinks(), service.getVolumes(), service.getExtra_hosts());
            dockerProxy.startContainer(containerId);

            if (!Strings.isNullOrEmpty(service.getBuild())) {
                if (isContainerRegisteredSuccessfullyInConsul(containerId)) {
                    removeCurrentContainer();
                }
            } else {
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
