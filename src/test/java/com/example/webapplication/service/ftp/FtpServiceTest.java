package com.example.webapplication.service.ftp;

import com.example.webapplication.config.ftp.properties.FtpProperties;
import com.example.webapplication.dto.ftp.FtpEntryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.file.remote.SessionCallback;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FtpServiceTest {

    public static final String TEST_FILE = "file.txt";
    public static final String TEST_TEXT = "hello";

    @Mock
    private FtpProperties ftpProperties;

    @Mock
    private FtpRemoteFileTemplate ftpRemoteFileTemplate;

    @InjectMocks
    private FtpService ftpService;

    @TempDir
    Path tempDir;

    @Test
    void list_ReturnFile() {

        FtpEntryDto entry = FtpEntryDto.builder()
                .name(TEST_FILE)
                .build();

        when(ftpRemoteFileTemplate.execute(any())).thenReturn(List.of(entry));

        List<FtpEntryDto> files = ftpService.list("");

        assertEquals(1, files.size());
        assertEquals(TEST_FILE, files.getFirst().getName());
    }


    @Test
    void downloadToLocalFile_shouldCallFtpTemplate() throws Exception {

        when(ftpProperties.getLocalDirectory()).thenReturn(tempDir.toString());

        when(ftpRemoteFileTemplate.execute(any())).thenReturn(null);

        Path result = ftpService.downloadToLocalFile(TEST_FILE);

        verify(ftpRemoteFileTemplate).execute(any());

        assertEquals(tempDir.resolve(TEST_FILE), result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldDownloadFile() throws Exception {

        when(ftpProperties.getLocalDirectory()).thenReturn(tempDir.toString());

        doAnswer(invocation -> {

            SessionCallback<Object, Object> callback = invocation.getArgument(0);
            Session<Object> session = mock(Session.class);

            doAnswer(readInvocation -> {
                OutputStream os = readInvocation.getArgument(1);
                os.write(TEST_TEXT.getBytes());
                return null;
            }).when(session).read(anyString(), any(OutputStream.class));

            callback.doInSession(session);
            return null;

        }).when(ftpRemoteFileTemplate).execute(any());

        Path result = ftpService.downloadToLocalFile(TEST_FILE);

        assertTrue(Files.exists(result));
        assertEquals(TEST_TEXT, Files.readString(result));
    }

    @Test
    @SuppressWarnings("unchecked")
    void downloadToBrowser_shouldReadFileFromFtp() throws Exception {
        Session<Object> session = mock(Session.class);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(ftpRemoteFileTemplate.execute(any())).thenAnswer(invocation -> {
            SessionCallback<Object, Object> callback = invocation.getArgument(0);
            return callback.doInSession(session);
        });

        ftpService.downloadToBrowser(TEST_TEXT, response);

        verify(session).read(eq(TEST_TEXT), any(OutputStream.class));
    }
}
