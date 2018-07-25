package com.github.howaric.docker_rapido.core;

import com.github.dockerjava.api.model.Container;
import com.github.howaric.docker_rapido.utils.CommonUtil;
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
        List<String> ports = service.getPorts();
        List<String> environment = service.getEnvironment();

        dockerProxy.pullImage(imageName, repository.getUsername(), repository.getPassword());

        String containerId = dockerProxy.createContainer(generateContainerName(),
                imageName.contains(ServiceTaskHandler.LATEST) ? imageName.replace(ServiceTaskHandler.LATEST, "") : imageName, ports,
                environment, service.getLinks(), service.getVolumes(), service.getExtra_hosts());
        logger.info("create container: " + containerId);
        dockerProxy.startContainer(containerId);
        // how to check if container is ready
    }

    @Override
    protected String generateContainerName() {
        return serviceName;
    }

    protected void findCurrentContainers() {
        List<Container> allRunningContainers = dockerProxy.listContainers(false);
        List<String> containerNames = new ArrayList<>();
        for (Container container : allRunningContainers) {
            String[] names = container.getNames();
            for (String name : names) {
                String realName = name.substring(1);
                if (!realName.contains("/") && realName.equals(getContainerNamePrefix())) {
                    current.add(container);
                    containerNames.add(realName);
                }
            }
        }
        logger.info("Find current containers: " + containerNames);
    }

}
