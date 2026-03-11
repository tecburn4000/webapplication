package com.example.webapplication.config.ftp;

import com.example.webapplication.config.ftp.properties.FtpProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;

@Configuration
@RequiredArgsConstructor
public class FtpConfig {

    private final FtpProperties properties;

    @Bean
    public DefaultFtpSessionFactory ftpSessionFactory() {
        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();

        factory.setHost(properties.getHost());
        factory.setPort(properties.getPort());
        factory.setUsername(properties.getUsername());
        factory.setPassword(properties.getPassword());

        factory.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
        factory.setFileType(FTP.BINARY_FILE_TYPE);

        return factory;
    }

    @Bean
    public FtpRemoteFileTemplate ftpRemoteFileTemplate(DefaultFtpSessionFactory ftpSessionFactory) {
        return new FtpRemoteFileTemplate(ftpSessionFactory);
    }
}