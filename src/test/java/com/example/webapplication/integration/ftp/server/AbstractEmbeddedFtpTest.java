package com.example.webapplication.integration.ftp.server;

import com.example.webapplication.config.ftp.properties.FtpProperties;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractEmbeddedFtpTest {

    public static final String DEFAULT_STRING = "default";

    @Autowired
    private FtpProperties ftpProperties;

    private FtpServer ftpServer;
    protected UserManager userManager;

    @BeforeAll
    void startEmbeddedFtp() throws FtpException {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(ftpProperties.getPort());
        serverFactory.addListener(DEFAULT_STRING, factory.createListener());
        userManager = serverFactory.getUserManager();
        ftpServer = serverFactory.createServer();
        ftpServer.start();
    }

    @AfterAll
    void stopEmbeddedFtp() {
        if (ftpServer != null) {
            ftpServer.stop();
        }
    }
}