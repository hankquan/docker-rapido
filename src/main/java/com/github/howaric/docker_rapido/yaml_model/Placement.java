package com.github.howaric.docker_rapido.yaml_model;

import java.util.List;

public class Placement {

	private List<String> constraints;

	public List<String> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<String> constraints) {
		this.constraints = constraints;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Placement [constraints=").append(constraints).append("]");
		return builder.toString();
	}

}
