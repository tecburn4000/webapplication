package com.example.webapplication.service.ftp;

import com.example.webapplication.config.ftp.properties.FtpProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FtpService {

    private final FtpRemoteFileTemplate ftpRemoteFileTemplate;
    private final FtpProperties ftpProperties;

    public List<String> listFiles() {
        return ftpRemoteFileTemplate.execute(session ->
                Arrays.stream(session.list(ftpProperties.getRemoteDirectory()))
                        .filter(file -> !file.isDirectory())
                        .map(FTPFile::getName)
                        .toList()
        );
    }
}