package com.github.howaric.docker_rapido.yaml_model;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

public class Deploy {

	private Placement placement;

	/**
	 * options: rolling-update, force-update, on-absence
	 */
	@NotBlank(message = "deploy_policy must be specified")
	private String deploy_policy;

	private RestartPolicy restart_policy;

	@Min(message = "replicas must be at least 1", value = 1)
	private Integer replicas;

	public RestartPolicy getRestart_policy() {
		return restart_policy;
	}

	public void setRestart_policy(RestartPolicy restart_policy) {
		this.restart_policy = restart_policy;
	}

	public Placement getPlacement() {
		return placement;
	}

	public void setPlacement(Placement placement) {
		this.placement = placement;
	}

	public String getDeploy_policy() {
		return deploy_policy;
	}

	public void setDeploy_policy(String deploy_policy) {
		this.deploy_policy = deploy_policy;
	}

	public Integer getReplicas() {
		return replicas;
	}

	public void setReplicas(Integer replicas) {
		this.replicas = replicas;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Deploy [placement=").append(placement).append(", deploy_policy=").append(deploy_policy)
				.append(", restart_policy=").append(restart_policy).append(", replicas=").append(replicas).append("]");
		return builder.toString();
	}

}
