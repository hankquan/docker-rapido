package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.exceptions.IllegalDeployPolicyException;

public enum DeployPolicy {

    ROLLING_UPDATE, FORCE_UPDATE, ON_ABSENCE;

    public DeployPolicy getType(String name) {
        DeployPolicy[] values = DeployPolicy.values();
        for (DeployPolicy deployPolicy : values) {
            if (deployPolicy.name().equalsIgnoreCase(name)) {
                return deployPolicy;
            }
        }
        throw new IllegalDeployPolicyException("Unsupported deploy policy:" + name);
    }

}
