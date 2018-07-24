package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.exceptions.UnsupportedTypeException;

public class DockerHostDeployerFactory {

    public static DockerHostDeployer getInstance(DeployPolicy deployPolicy) {
        switch (deployPolicy) {
        case ROLLING_UPDATE:
            return new RollingUpdateDockerHostDeployer();
        case ON_ABSENCE:
            return new OnAbsenceDockerHostDeployer();
        case FORCE_UPDATE:
            throw new UnsupportedTypeException("DeployPolicy force-update is not supported yet");
        default:
            throw new UnsupportedTypeException("Illegal deployPolicy type");
        }
    }

}
