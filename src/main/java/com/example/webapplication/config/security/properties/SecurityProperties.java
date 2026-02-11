package com.example.webapplication.config.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private boolean permitsAll = false;

    public boolean isPermitsAll() {
        return permitsAll;
    }

    public void setPermitsAll(boolean permitsAll) {
        this.permitsAll = permitsAll;
    }

}