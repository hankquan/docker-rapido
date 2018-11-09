package com.github.howaric.docker_rapido.core;

import java.util.Arrays;

import com.github.howaric.docker_rapido.exceptions.UnsupportedTypeException;

public enum DeliveryType {

    OFFICIAL("official"), DEVELOPMENTAL("developmental"), REGRESSION("regression");

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

    public boolean isOfficial() {
        if (value.equals(OFFICIAL.value)) {
            return true;
        }
        return false;
    }

    public boolean isDevelopmental() {
        if (value.equals(DEVELOPMENTAL.value)) {
            return true;
        }
        return false;
    }
    
    public boolean isRegression() {
        if (value.equals(REGRESSION.value)) {
            return true;
        }
        return false;
    }

    public static DeliveryType getType(String type) {
        try {
            return DeliveryType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new UnsupportedTypeException("Unsupported delivery type: " + type + " | "
                    + Arrays.asList(OFFICIAL.value, DEVELOPMENTAL.value, REGRESSION.value), e);
        }
    }

    public static void main(String[] args) {
        DeliveryType type = DeliveryType.getType("regression");
        System.out.println(type.isRegression());
    }
}