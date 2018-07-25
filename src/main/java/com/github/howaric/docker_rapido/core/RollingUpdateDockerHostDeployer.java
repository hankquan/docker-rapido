package com.github.howaric.docker_rapido.core;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
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

            dockerProxy.pullImage(imageName, repository.getUsername(), repository.getPassword());

            String containerId = dockerProxy.createContainer(generateContainerName(), imageName, ports, environment, service.getLinks(),
                    service.getVolumes(), service.getExtra_hosts());
            dockerProxy.startContainer(containerId);

            InspectContainerResponse inspectContainer = dockerProxy.inspectContainer(containerId);
            ContainerNetwork containerNetwork = inspectContainer.getNetworkSettings().getNetworks().get("bridge");
            if (containerNetwork != null) {
                String containerIp = containerNetwork.getIpAddress();
                logger.info("Get container IP: {}", containerIp);
                if (isContainerSuccessfullyRegisteredToConsul(containerIp)) {
                    removeCurrentContainer();
                }
            } else {
                logger.warn("Get container Ip failed, container might not be up");
            }
        }
        if (!Strings.isNullOrEmpty(service.getPublish_port())) {
            logger.info("Start to check service {}", serviceName);
            CommonUtil.sleep(5000);
            isServiceOnPortReady(node.getIp(), service.getPublish_port());
        }
        while (CommonUtil.hasElement(current)) {
            removeCurrentContainer();
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
