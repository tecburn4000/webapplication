package com.example.webapplication.service.ftp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FtpServiceTest {

    //file zilla ftps ==> sftp
    @Mock
    private FtpRemoteFileTemplate ftpRemoteFileTemplate;

    @InjectMocks
    private FtpService ftpService;

    @Test
    void shouldReturnFileList() {

        when(ftpRemoteFileTemplate.execute(any())).thenReturn(List.of("file1.txt"));

        List<String> result = ftpService.listFiles();

        assertEquals(1, result.size());
        assertEquals("file1.txt", result.getFirst());
    }
}