package com.github.howaric.docker_rapido.yaml_model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ServiceCheck {

    /**
     * will use node ip and port as base url
     */
    private String uri;

    /**
     * disable container service check as default
     */
    private Boolean disable = true;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Boolean isDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("uri", uri).append("disable", disable).toString();
    }

}
