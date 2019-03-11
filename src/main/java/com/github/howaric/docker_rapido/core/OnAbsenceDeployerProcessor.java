package com.github.howaric.docker_rapido.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.google.common.base.Strings;

public class OnAbsenceDeployerProcessor extends DeployProcessor {

    private static Logger logger = LoggerFactory.getLogger(OnAbsenceDeployerProcessor.class);

    @Override
    protected void perform() {
        clearCurrentContainerList();
        findCurrentContainers(false);
        if (CommonUtil.hasElement(current)) {
            logger.info("Service {} has already existed, deployment skipped", serviceName);
            return;
        }
        String imageExited = dockerProxy.isImageExsited(imageName);
        if (Strings.isNullOrEmpty(imageExited)) {
            dockerProxy.pullImage(imageName, repository.getUsername(), repository.getPassword());
        }
        String containerId = dockerProxy.createContainer(generateContainerName(),
                imageName.contains(DeployTaskHandler.LATEST) ? imageName.replace(DeployTaskHandler.LATEST, "") : imageName, service);
        dockerProxy.startContainer(containerId);

        checkContainerStatusAfterOneMinute(containerId);
    }

}
