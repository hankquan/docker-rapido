package com.github.howaric.docker_rapido.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.NetworkSettings;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.howaric.docker_rapido.utils.CommonUtil;

public class RollingUpdateRapidoDockerRunner extends AbstractRapidoDockerRunner {

    private static Logger logger = LoggerFactory.getLogger(RollingUpdateRapidoDockerRunner.class);

    @Override
    protected void perform() {
        Integer replicas = service.getDeploy().getReplicas();
        for (int i = 1; i <= replicas; i++) {
            List<String> ports = service.getPorts();
            List<String> environment = service.getEnvironment();
            if (!dockerProxy.isImageExits(imageName)) {
                dockerProxy.pullImage(imageName, "", "");
            }
            String containerId = dockerProxy.createContainer(generateContainerName(), imageName, ports, environment,
                    service.getLinks(), service.getVolumes(), service.getExtra_hosts());
            logger.info("create container: " + containerId);
            dockerProxy.startContainer(containerId);
            InspectContainerResponse containerDetails = dockerProxy.inspectContainer(containerId);
            NetworkSettings networkSettings = containerDetails.getNetworkSettings();
            Ports exposedPorts = networkSettings.getPorts();
            Map<ExposedPort, Binding[]> bindings = exposedPorts.getBindings();
            Collection<Binding[]> values = bindings.values();
            String containerExposedPort = "0";
            for (Binding[] binding : values) {
                containerExposedPort = binding[0].getHostPortSpec();
            }
            if (isContainerReady(containerExposedPort)) {
                removeCurrentContainer();
            }
        }
        while (CommonUtil.hasElement(current)) {
            removeCurrentContainer();
        }
    }

    protected void findCurrentContainers() {
        List<Container> allRunningContainers = dockerProxy.listContainers(false);
        for (Container container : allRunningContainers) {
            String containerName = container.getNames()[0].substring(1);
            if (containerName.startsWith(getContainerNamePrefix())) {
                current.add(container);
            }
        }
        logger.info("Find current containers: " + current);
    }

}
