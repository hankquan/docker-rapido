package com.github.howaric.docker_rapido.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.model.Container;
import com.github.howaric.docker_rapido.docker.DockerProxy;
import com.github.howaric.docker_rapido.docker.DockerProxyFactory;
import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.Repository;
import com.github.howaric.docker_rapido.yaml_model.Service;
import com.google.common.base.Strings;

public abstract class AbstractNodeProcessor implements NodeProcessor {

    private static Logger logger = LoggerFactory.getLogger(AbstractNodeProcessor.class);

    protected DockerProxy dockerProxy;

    protected LinkedList<Container> current = new LinkedList<>();

    protected String serviceName;
    protected DeliveryType deliveryType;
    protected String owner;
    protected String imageName;
    protected Node node;
    protected Service service;
    protected Repository repository;

    @Override
    public void process(ProcessorInfo processorInfo) {
        this.repository = processorInfo.getRepository();
        this.deliveryType = processorInfo.getDeliveryType();
        this.owner = processorInfo.getOwner();
        this.node = processorInfo.getNode();
        this.service = processorInfo.getService();
        this.serviceName = processorInfo.getServiceName();
        this.imageName = processorInfo.getImageNameWithRepoAndTag();
        String dockerEndPoint = node.dockerEndPoint();
        dockerProxy = DockerProxyFactory.getInstance(dockerEndPoint);
        findCurrentContainers(true);
        String exitedImageId = dockerProxy.isImageExited(imageName);
        perform();
        dockerProxy.tryToRemoveImage(exitedImageId);
    }

    protected abstract void perform();

    protected void findCurrentContainers(boolean isContainRunning) {
        List<Container> allContainers = dockerProxy.listContainers(isContainRunning);
        List<String> containerNames = new ArrayList<>();
        for (Container container : allContainers) {
            String[] names = container.getNames();
            for (String name : names) {
                String realName = name.substring(1);
                if (!realName.contains("/")
                        && (realName.startsWith(getContainerNamePrefix()) || realName.equals(generateContainerName()))) {
                    current.add(container);
                    containerNames.add(realName);
                }
            }
        }
        String statues = isContainRunning ? "All" : "Running";
        logger.info("Find current service containers(" + statues + "): " + containerNames);
    }

    protected String generateContainerName() {
        if (Strings.isNullOrEmpty(service.getBuild())) {
            return serviceName;
        }
        return getContainerNamePrefix() + "." + CommonUtil.getTimeStamp();
    }

    private String getContainerNamePrefix() {
        return serviceName + "." + deliveryType.getValue() + "." + owner;
    }

    protected void removeCurrentContainer() {
        Container container = current.poll();
        try {
            dockerProxy.stopContainer(container.getId(), service.getDeploy().getStop_timeout());
            dockerProxy.removeContainer(container.getId());
            logger.info("Stop and remove old container: " + container.getNames()[0].substring(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void clearCurrentContainerList() {
        current.clear();
    }

}
