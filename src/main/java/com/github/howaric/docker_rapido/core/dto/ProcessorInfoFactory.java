package com.github.howaric.docker_rapido.core.dto;

import com.github.howaric.docker_rapido.core.DeliveryType;
import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.Repository;
import com.github.howaric.docker_rapido.yaml_model.Service;

public class ProcessorInfoFactory {

    public static ProcessorInfo getCleanProcessorInfo(DeliveryType deliveryType, String owner, Node node, String serviceName) {
        return new ProcessorInfo(deliveryType, owner, node, serviceName);
    }

    public static ProcessorInfo getDeployProcessorInfo(Repository repository, DeliveryType deliveryType, String owner, Node node,
            String serviceName, Service service, String imageNameWithRepoAndTag) {
        return new ProcessorInfo(repository, deliveryType, owner, node, serviceName, service, imageNameWithRepoAndTag);
    }

}
