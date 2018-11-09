package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.Repository;
import com.github.howaric.docker_rapido.yaml_model.Service;

public interface DockerHostDeployer {

    void deploy(Repository repository, DeliveryType deliveryType, String owner, Node node, String serviceName, Service service,
            String imageNameWithRepoAndTag);

}
