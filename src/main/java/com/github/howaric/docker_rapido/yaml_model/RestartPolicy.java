package com.github.howaric.docker_rapido.yaml_model;

public class RestartPolicy {

	private String condition;

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RestartPolicy [condition=").append(condition).append("]");
		return builder.toString();
	}

}
