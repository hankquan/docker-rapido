package com.github.howaric.docker_rapido.core;

import com.github.dockerjava.api.model.Container;
import com.github.howaric.docker_rapido.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OnAbsenceRapidoDockerRunner extends AbstractRapidoDockerRunner {

    private static Logger logger = LoggerFactory.getLogger(OnAbsenceRapidoDockerRunner.class);

    @Override
    protected void perform() {
        if (CommonUtil.hasElement(current)) {
            logger.info("Service {} has already existed, deployment skipped", serviceName);
            return;
        }
        List<String> ports = service.getPorts();
        List<String> environment = service.getEnvironment();

        if (!dockerProxy.isImageExits(imageName)) {
            dockerProxy.pullImage(imageName, "", "");
        }

        String containerId = dockerProxy.createContainer(generateContainerName(),
                imageName.contains(ServiceTaskHandler.LATEST) ? imageName.replace(ServiceTaskHandler.LATEST, "") : imageName, ports, environment,
                service.getLinks(), service.getVolumes(), service.getExtra_hosts());
        logger.info("create container: " + containerId);
        dockerProxy.startContainer(containerId);
        // how to check if container is ready
    }

    @Override
    protected String generateContainerName() {
        return getContainerNamePrefix();
    }

    protected void findCurrentContainers() {
        List<Container> allRunningContainers = dockerProxy.listContainers(false);
        for (Container container : allRunningContainers) {
            String containerName = container.getNames()[0].substring(1);
            if (containerName.equals(generateContainerName())) {
                current.add(container);
            }
        }
        logger.info("Find current containers: " + current);
    }

}
