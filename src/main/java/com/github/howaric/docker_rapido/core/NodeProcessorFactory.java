package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.exceptions.UnsupportedTypeException;

public class NodeProcessorFactory {

    public static NodeProcessor getDeployProcessor(DeployPolicy deployPolicy) {
        switch (deployPolicy) {
        case ROLLING_UPDATE:
            return new RollingUpdateDeployerProcessor();
        case ON_ABSENCE:
            return new OnAbsenceDeployerProcessor();
        case FORCE_UPDATE:
            return new ForceUpdateDeployerProcessor();
        default:
            throw new UnsupportedTypeException("Illegal deployPolicy type");
        }
    }

    public static NodeProcessor getCleanProcessor() {
        return new CommonCleanProcessor();
    }

}
