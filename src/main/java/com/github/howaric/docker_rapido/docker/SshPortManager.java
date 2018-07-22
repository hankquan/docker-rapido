package com.github.howaric.docker_rapido.docker;

import com.github.howaric.docker_rapido.yaml_model.Node;

public class SshPortManager implements PortManager {

	@Override
	public Integer getPort(Node node) {

		return 31000;
	}

}
