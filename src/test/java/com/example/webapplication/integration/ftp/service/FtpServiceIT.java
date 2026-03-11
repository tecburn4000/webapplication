package com.example.webapplication.integration.ftp.service;

import com.example.webapplication.config.ftp.properties.FtpProperties;
import com.example.webapplication.dto.ftp.FtpEntry;
import com.example.webapplication.integration.ftp.server.AbstractEmbeddedFtpTest;
import com.example.webapplication.service.ftp.FtpService;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;


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

    private Path tempFilePath;

    @BeforeEach
    void setUp() throws FtpException, IOException {

        // setup test dir in Junit @Tempdir
        tempFilePath = Files.createDirectories(tempDir.resolve(ftpProperties.getRemoteDirectory()));
        Files.writeString(tempFilePath.resolve(TEST_FILE), TEST_TXT);

        // create FTP user for testing
        BaseUser user = new BaseUser();
        user.setName(ftpProperties.getUsername());
        user.setPassword(ftpProperties.getPassword());
        user.setHomeDirectory(tempFilePath.toFile().getAbsolutePath());
        user.setAuthorities(List.of(new WritePermission()));
        userManager.save(user);
    }

    @Test
    void shouldListFilesFromEmbeddedFtp() {
        List<FtpEntry> files = ftpService.list(""); // use empty string while ftp dir is already set in setUp()
        assertThat(files).anyMatch(ftpEntry -> ftpEntry.getName().equals(TEST_FILE));
    }

    @Test
    void shouldDownloadFileToLocalDirectory() throws Exception {
        String downloadDir = tempFilePath + "/" + ftpProperties.getLocalDirectory();
        ftpProperties.setLocalDirectory(downloadDir); // point download dir to @TempDir
        Path downloaded = ftpService.downloadToLocalFile(TEST_FILE);
        String content = Files.readString(downloaded);
        assertThat(content).isEqualTo(TEST_TXT);
    }

    @Test
    void shouldDownloadToBrowser() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ftpService.downloadToBrowser(TEST_FILE, response);
        byte[] expected = TEST_TXT.getBytes();
        byte[] actual = response.getContentAsByteArray();
        assertArrayEquals(expected, actual);
    }
}