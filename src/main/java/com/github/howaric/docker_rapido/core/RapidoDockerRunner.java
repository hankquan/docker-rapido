package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.Service;

public interface RapidoDockerRunner {

    class RapidoDockerRunnerFactory {
        public static RapidoDockerRunner getInstance(DeployPolicy deployPolicy) {
            switch (deployPolicy) {
                case ROLLING_UPDATE:
                    return new RollingUpdateRapidoDockerRunner();
                case ON_ABSENCE:
                    return new OnAbsenceRapidoDockerRunner();
                default:
                    return null;
            }
        }
    }

    void start(String deployType, String owner, Node node, String serviceName, Service service, String imageNameWithRepoAndTag);

}
