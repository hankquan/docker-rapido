package com.github.howaric.docker_rapido.yaml_model;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

import com.github.howaric.docker_rapido.core.DeployPolicy;

public class Deploy {

	private Placement placement;

	/**
	 * options: rolling-update, force-update, on-absence(default)
	 */
	@NotBlank(message = "deploy_policy must be specified")
	private String deploy_policy;

	private Integer stop_timeout;

	private RestartPolicy restart_policy;

	@Min(message = "replicas must be at least 1", value = 1)
	private Integer replicas = 1;

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

	public Integer getStop_timeout() {
		return stop_timeout;
	}

	public void setStop_timeout(Integer stop_timeout) {
		this.stop_timeout = stop_timeout;
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
		final StringBuilder sb = new StringBuilder("Deploy{");
		sb.append("placement=").append(placement);
		sb.append(", deploy_policy='").append(deploy_policy).append('\'');
		sb.append(", stop_timeout=").append(stop_timeout);
		sb.append(", restart_policy=").append(restart_policy);
		sb.append(", replicas=").append(replicas);
		sb.append('}');
		return sb.toString();
	}
}
