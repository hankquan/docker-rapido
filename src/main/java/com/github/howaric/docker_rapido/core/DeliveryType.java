package com.github.howaric.docker_rapido.core;

import java.util.Arrays;
import java.util.List;

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

    public static boolean isDeliveryTypeLegal(String type) {
        DeliveryType[] values = DeliveryType.values();
        for (DeliveryType deliveryType : values) {
            if (deliveryType.getValue().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> supportedTypes() {
        return Arrays.asList(OFFICIAL.getValue(), DEVELOPMENTAL.getValue());
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