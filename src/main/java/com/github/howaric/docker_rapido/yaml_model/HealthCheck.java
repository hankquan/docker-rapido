package com.github.howaric.docker_rapido.yaml_model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class HealthCheck {

    private Boolean disable = false;

    public Boolean isDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("disable", disable).toString();
    }
}
