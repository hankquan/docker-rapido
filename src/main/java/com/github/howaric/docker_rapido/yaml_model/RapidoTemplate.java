package com.github.howaric.docker_rapido.yaml_model;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

public class RapidoTemplate {

    @NotBlank(message = "version can not be empty")
    @Pattern(regexp = "^[0-9]{1}.[0-9]{1,2}$", message = "verion pattern is not correct, examlpe: 0.1, 1.0")
    private String version;

    @NotBlank(message = "deliver_type can not be empty")
    private String deliver_type;

    @NotBlank(message = "owner can not be empty")
    private String owner;

    @Valid
    private Repository repository;

    @NotBlank(message = "remote_docker can not be empty")
    private String remote_docker;

    @Valid
    private Map<String, Service> services;

    @Valid
    private Map<String, Node> nodes;

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public String getRemote_docker() {
        return remote_docker;
    }

    public void setRemote_docker(String remote_docker) {
        this.remote_docker = remote_docker;
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, Node> nodes) {
        this.nodes = nodes;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Map<String, Service> getServices() {
        return services;
    }

    public void setServices(Map<String, Service> services) {
        this.services = services;
    }

    public String getDeliver_type() {
        return deliver_type;
    }

    public void setDeliver_type(String deliver_type) {
        this.deliver_type = deliver_type;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RapidoTemplate [version=").append(version).append(", deliver_type=").append(deliver_type).append(", owner=")
                .append(owner).append(", repository=").append(repository).append(", remote_docker=").append(remote_docker)
                .append(", services=").append(services).append(", nodes=").append(nodes).append("]");
        return builder.toString();
    }

}
