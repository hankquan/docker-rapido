package com.github.howaric.docker_rapido.yaml_model;

import java.util.List;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;

public class Service {

    @NotBlank(message = "image can not be null")
    private String image;
    private String build;
    private String publish_port;
    private List<String> ports;
    private List<String> extra_hosts;
    private List<String> depends_on;
    private List<String> volumes;
    private List<String> environment;
    private List<String> links;

    @Valid
    private Deploy deploy;

    public Integer firstExposedPort() {
        for (String port : ports) {
            if (!port.contains(":")) {
                return Integer.valueOf(port);
            }
        }
        return 0;
    }

    public String getPublish_port() {
        return publish_port;
    }

    public void setPublish_port(String publish_port) {
        this.publish_port = publish_port;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public Deploy getDeploy() {
        return deploy;
    }

    public void setDeploy(Deploy deploy) {
        this.deploy = deploy;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }

    public List<String> getExtra_hosts() {
        return extra_hosts;
    }

    public void setExtra_hosts(List<String> extra_hosts) {
        this.extra_hosts = extra_hosts;
    }

    public List<String> getDepends_on() {
        return depends_on;
    }

    public void setDepends_on(List<String> depends_on) {
        this.depends_on = depends_on;
    }

    public List<String> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<String> volumes) {
        this.volumes = volumes;
    }

    public List<String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(List<String> environment) {
        this.environment = environment;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Service [image=").append(image).append(", build=").append(build).append(", publish_port=").append(publish_port)
                .append(", ports=").append(ports).append(", extra_hosts=").append(extra_hosts).append(", depends_on=").append(depends_on)
                .append(", volumes=").append(volumes).append(", environment=").append(environment).append(", links=").append(links)
                .append(", deploy=").append(deploy).append("]");
        return builder.toString();
    }

}
