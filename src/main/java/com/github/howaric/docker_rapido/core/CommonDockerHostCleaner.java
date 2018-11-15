package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.docker.DockerProxy;
import com.github.howaric.docker_rapido.docker.DockerProxyFactory;
import com.github.howaric.docker_rapido.yaml_model.Node;

public class CommonDockerHostCleaner {

    public void clean(DeliveryType deliveryType, String owner, Node node, String serviceName) {
        DockerProxy dockerProxy = DockerProxyFactory.getInstance(node.dockerEndPoint());
        
        
    }
    
}
