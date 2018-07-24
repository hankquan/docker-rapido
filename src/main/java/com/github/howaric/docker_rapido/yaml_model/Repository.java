package com.github.howaric.docker_rapido.yaml_model;

import com.google.common.base.Strings;

public class Repository {

    private String url;
    private String prefix;
    private String username;
    private String password;

    public String getRepo() {
        if (Strings.isNullOrEmpty(prefix)) {
            return url;
        } else {
            return url + "/" + prefix;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Repository [url=").append(url).append(", prefix=").append(prefix).append(", username=").append(username)
                .append(", password=").append(password).append("]");
        return builder.toString();
    }

}
