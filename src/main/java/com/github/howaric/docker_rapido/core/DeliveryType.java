package com.github.howaric.docker_rapido.core;

public enum DeliveryType {

	OFFICIAL("official"), DEVELOPMENTAL("developmental");

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private DeliveryType(String value) {
		this.value = value;
	}

	public static boolean isOfficial(String type) {
		if (OFFICIAL.getValue().equalsIgnoreCase(type.trim())) {
			return true;
		}
		return false;
	}

	public static boolean isDevelopmental(String type) {
		if (DEVELOPMENTAL.getValue().equalsIgnoreCase(type.trim())) {
			return true;
		}
		return false;
	}

}