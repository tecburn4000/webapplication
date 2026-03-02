package com.example.webapplication.config.ftp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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