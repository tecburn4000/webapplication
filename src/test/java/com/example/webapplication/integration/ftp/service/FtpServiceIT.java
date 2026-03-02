package com.example.webapplication.integration.ftp.service;

import com.example.webapplication.config.ftp.properties.FtpProperties;
import com.example.webapplication.integration.ftp.server.AbstractEmbeddedFtpTest;
import com.example.webapplication.service.ftp.FtpService;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
class FtpServiceIT extends AbstractEmbeddedFtpTest {

    public static final String TEST_FILE = "test.txt";
    public static final String TEST_TXT = "Hello FTP";

    @Autowired
    private FtpService ftpService;

    @Autowired
    private FtpProperties ftpProperties;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws FtpException, IOException {

        // setup test dir in Junit @Tempdir
        Path remoteDir = Files.createDirectories(tempDir.resolve(ftpProperties.getRemoteDirectory()));
        Files.writeString(remoteDir.resolve(TEST_FILE), TEST_TXT);

        // setup FTP user for testing
        BaseUser user = new BaseUser();
        user.setName(ftpProperties.getUsername());
        user.setPassword(ftpProperties.getPassword());
        user.setHomeDirectory(tempDir.toFile().getAbsolutePath());
        user.setAuthorities(List.of(new WritePermission()));
        userManager.save(user);
    }

    @Test
    void shouldListFilesFromEmbeddedFtp() {
        List<String> files = ftpService.listFiles();
        assertThat(files).contains(TEST_FILE);
    }
}