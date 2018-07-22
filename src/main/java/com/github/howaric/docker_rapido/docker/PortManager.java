package com.github.howaric.docker_rapido.docker;

import com.github.howaric.docker_rapido.yaml_model.Node;

public interface PortManager {

	Integer getPort(Node node);

}
