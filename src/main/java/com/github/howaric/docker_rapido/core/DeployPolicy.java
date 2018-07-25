package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.exceptions.UnsupportedTypeException;
import com.google.common.base.Strings;

public enum DeployPolicy {

	ROLLING_UPDATE("rolling-update"), FORCE_UPDATE("force-update"), ON_ABSENCE("on-absence");

	private String value;

	private DeployPolicy(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static DeployPolicy getType(String policy) {
		if (Strings.isNullOrEmpty(policy)) {
			return ON_ABSENCE;
		}
		DeployPolicy[] values = DeployPolicy.values();
		for (DeployPolicy deployPolicy : values) {
			if (deployPolicy.getValue().equalsIgnoreCase(policy)) {
				return deployPolicy;
			}
		}
		throw new UnsupportedTypeException("Unknown deploy_policy: " + policy);
	}

}
