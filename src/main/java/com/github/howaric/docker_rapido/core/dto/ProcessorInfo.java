package com.github.howaric.docker_rapido.core.dto;

import com.github.howaric.docker_rapido.core.DeliveryType;
import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.Repository;
import com.github.howaric.docker_rapido.yaml_model.Service;

public class ProcessorInfo {

    private Repository repository;
    private DeliveryType deliveryType;
    private String owner;
    private Node node;
    private String serviceName;
    private Service service;
    private String imageNameWithRepoAndTag;

    ProcessorInfo(Repository repository, DeliveryType deliveryType, String owner, Node node, String serviceName, Service service,
            String imageNameWithRepoAndTag) {
        super();
        this.repository = repository;
        this.deliveryType = deliveryType;
        this.owner = owner;
        this.node = node;
        this.serviceName = serviceName;
        this.service = service;
        this.imageNameWithRepoAndTag = imageNameWithRepoAndTag;
    }

    ProcessorInfo(DeliveryType deliveryType, String owner, Node node, String serviceName) {
        super();
        this.deliveryType = deliveryType;
        this.owner = owner;
        this.node = node;
        this.serviceName = serviceName;
    }

    public Repository getRepository() {
        return repository;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public String getOwner() {
        return owner;
    }

    public Node getNode() {
        return node;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Service getService() {
        return service;
    }

    public String getImageNameWithRepoAndTag() {
        return imageNameWithRepoAndTag;
    }

}
