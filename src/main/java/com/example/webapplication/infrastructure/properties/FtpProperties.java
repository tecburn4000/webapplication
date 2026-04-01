package com.example.webapplication.infrastructure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ftp")
public class FtpProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private String remoteDirectory;
    private String localDirectory;
    private Integer timeout;
    private boolean passiveMode;
}

