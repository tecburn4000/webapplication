package com.example.webapplication.config.security.spel;

import com.example.webapplication.config.security.properties.SecurityProperties;
import org.springframework.stereotype.Component;

@Component("securityPropertiesPermitsAll") // Name für SpEL
public class SecurityPropertiesSpEL {

    private final SecurityProperties securityProperties;

    public SecurityPropertiesSpEL(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public boolean get() {
        return securityProperties.isPermitsAll();
    }
}
