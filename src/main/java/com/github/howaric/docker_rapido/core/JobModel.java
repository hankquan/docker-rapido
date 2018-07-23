package com.github.howaric.docker_rapido.core;

import java.util.List;

import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.Service;

public class JobModel {

    private String remote_docker;
    private String repo;
    private String deliver_type;
    private String owner;
    private String serviceName;
    private Service service;
    private List<Node> targetNodes;
    private String imageTag;

    public JobModel(String remote_docker, String repo, String deliver_type, String owner, String serviceName, Service service,
            List<Node> targetNodes, String imageTag) {
        super();
        this.remote_docker = remote_docker;
        this.repo = repo;
        this.deliver_type = deliver_type;
        this.owner = owner;
        this.serviceName = serviceName;
        this.service = service;
        this.targetNodes = targetNodes;
        this.imageTag = imageTag;
    }

    public String getDeliver_type() {
        return deliver_type;
    }

    public void setDeliver_type(String deliver_type) {
        this.deliver_type = deliver_type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public List<Node> getTargetNodes() {
        return targetNodes;
    }

    public void setTargetNodes(List<Node> targetNodes) {
        this.targetNodes = targetNodes;
    }

    public String getImageTag() {
        return imageTag;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }

    @Override
    public String toString() {
        return "JobModel [remote_docker=" + remote_docker + ", repo=" + repo + ", deliver_type=" + deliver_type + ", owner=" + owner
                + ", serviceName=" + serviceName + ", service=" + service + ", targetNodes=" + targetNodes + ", imageTag=" + imageTag + "]";
    }

}
