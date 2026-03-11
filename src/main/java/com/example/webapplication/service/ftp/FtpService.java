package com.example.webapplication.service.ftp;

import com.example.webapplication.config.ftp.properties.FtpProperties;
import com.example.webapplication.dto.ftp.Breadcrumb;
import com.example.webapplication.dto.ftp.FileInfo;
import com.example.webapplication.dto.ftp.FtpEntry;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FtpService {

    private final FtpRemoteFileTemplate ftpRemoteFileTemplate;
    private final FtpProperties ftpProperties;

    public List<FtpEntry> list(String path) {
        return ftpRemoteFileTemplate.execute(session -> {
            FTPFile[] files = session.list(path);
            return Arrays.stream(files)
                    .map(f -> FtpEntry.builder()
                            .name(f.getName())
                            .path(path + "/" + f.getName())
                            .directory(f.isDirectory())
                            .size(f.getSize())
                            .modified(f.getTimestamp().toInstant())
                            .build()
                    )
                    .sorted(Comparator
                    .comparing(FtpEntry::isDirectory).reversed()
                    .thenComparing(FtpEntry::getName))
                    .toList();
        });
    }

    public List<Breadcrumb> breadcrumbs(String path) {

        List<Breadcrumb> crumbs = new ArrayList<>();

        crumbs.add(new Breadcrumb("root", "/"));

        if ("/".equals(path)) return crumbs;

        String[] parts = path.split("/");
        StringBuilder current = new StringBuilder();

        for (String p : parts) {

            if (p.isBlank()) continue;

            current.append("/").append(p);
            crumbs.add(new Breadcrumb(p, current.toString()));
        }

        return crumbs;
    }

    private void streamFile(String remotePath, OutputStream outputStream) {
        ftpRemoteFileTemplate.execute(session -> {
            session.read(remotePath, outputStream);
            return null;
        });
    }

    public Path downloadToLocalFile(String filePath) throws IOException {
        Path targetDirectory = Path.of(ftpProperties.getLocalDirectory());
        Files.createDirectories(targetDirectory);
        Path downloadFile = targetDirectory.resolve(filePath);
        try (OutputStream os = Files.newOutputStream(downloadFile)) {
            streamFile(filePath, os);
        }
        return downloadFile;
    }

    public void downloadToBrowser(String filename, HttpServletResponse response) {
        ftpRemoteFileTemplate.execute(session -> {
            try (OutputStream out = response.getOutputStream()) {
                session.read(filename, out); // FTP -> HTTP Stream
                out.flush();
            }
            return null;
        });
    }

}
