package com.github.howaric.docker_rapido.core;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.howaric.docker_rapido.exceptions.ContainerStartingFailedException;
import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OnAbsenceDockerHostDeployer extends AbstractDockerHostDeployer {

    private static Logger logger = LoggerFactory.getLogger(OnAbsenceDockerHostDeployer.class);

    @Override
    protected void perform() {
        if (CommonUtil.hasElement(current)) {
            logger.info("Service {} has already existed, deployment skipped", serviceName);
            return;
        }
        String imageExited = dockerProxy.isImageExited(imageName);
        if (Strings.isNullOrEmpty(imageExited)) {
            dockerProxy.pullImage(imageName, repository.getUsername(), repository.getPassword());
        }
        String containerId = dockerProxy.createContainer(generateContainerName(),
                imageName.contains(ServiceTaskHandler.LATEST) ? imageName.replace(ServiceTaskHandler.LATEST, "") : imageName,
                service.getDeploy().getRestart_policy().getCondition(), service.getPorts(), service.getEnvironment(), service.getLinks(),
                service.getVolumes(), service.getExtra_hosts());
        dockerProxy.startContainer(containerId);

        CommonUtil.sleep(15000);
        // check if still running
        /*
        InspectContainerResponse inspectContainer = dockerProxy.inspectContainer(containerId);
        String status = inspectContainer.getState().getStatus();
        if (!isContainerUp(status)) {
            throw new ContainerStartingFailedException("Starting " + serviceName + " failed");
        } 
         */
        
        // print logs
        //printContainerStartingLogs(containerId);
    }

    @Override
    protected String generateContainerName() {
        return serviceName;
    }

    @Override
    protected void findCurrentContainers() {
        List<Container> allRunningContainers = dockerProxy.listContainers(false);
        List<String> containerNames = new ArrayList<>();
        for (Container container : allRunningContainers) {
            String[] names = container.getNames();
            for (String name : names) {
                String realName = name.substring(1);
                if (!realName.contains("/") && realName.equals(generateContainerName())) {
                    current.add(container);
                    containerNames.add(realName);
                }
            }
        }
        logger.info("Find current containers: " + containerNames);
    }

}
