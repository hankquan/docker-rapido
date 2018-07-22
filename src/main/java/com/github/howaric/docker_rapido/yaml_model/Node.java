package com.github.howaric.docker_rapido.yaml_model;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

public class Node {

	/**
	 * ip: 39.104.164.56 username: root password: 12345ddD docker_port: 6066 labels:
	 * - site=Ki
	 */
	@NotBlank(message = "version can not be empty")
	@Pattern(regexp = "^((25[0-5]|2[0-4]\\d|[1]{1}\\d{1}\\d{1}|[1-9]{1}\\d{1}|\\d{1})($|(?!\\.$)\\.)){4}$", message = "ip is illegal")
	private String ip;
	private String username;
	private String password;

	@Min(message = "docker port shout be at least 2375", value = 2375)
	@Max(message = "docker port can not be larger than 65535", value = 65535)
	private Integer docker_port;
	private List<String> labels;

	private static final String endPointTemplate = "tcp://%s:%s";

	public boolean hasLabel(String key, String value) {
		if (labels == null) {
			return false;
		}
		if (labels.contains(key + "=" + value)) {
			return true;
		}
		return false;
	}

	public String getDockerEndPoint() {
		return String.format(endPointTemplate, ip, docker_port);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getDocker_port() {
		return docker_port;
	}

	public void setDocker_port(Integer docker_port) {
		this.docker_port = docker_port;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	@Override
	public String toString() {
		return "Node [ip=" + ip + ", username=" + username + ", password=" + password + ", docker_port=" + docker_port
				+ ", labels=" + labels + "]";
	}

}
