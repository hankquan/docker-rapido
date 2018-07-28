package com.github.howaric.docker_rapido.docker;

import java.util.ArrayList;
import java.util.List;

public enum RestartPolicy {

    NO("no"), ALWAYS("always"), ON_FAILURE("on-failure"), UNLESS_STOPPED("unless-stopped");

    private String value;

    private RestartPolicy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static boolean isRestartPolicyLegal(String policy) {
        RestartPolicy[] values = RestartPolicy.values();
        for (RestartPolicy restartPolicy : values) {
            if (policy.equals(restartPolicy.getValue()) || policy.startsWith(restartPolicy.getValue())) {
                return true;
            }
        }
        return false;
    }

    private static final List<String> supportedTypes = new ArrayList<>();
    
    public static List<String> supportedTypes() {
        if (supportedTypes.isEmpty()) {
            RestartPolicy[] values = RestartPolicy.values();
            for (RestartPolicy restartPolicy : values) {
                supportedTypes.add(restartPolicy.getValue());
            }
        }
        return supportedTypes;
    }

}
