package com.example.webapplication.service.ftp;

import com.example.webapplication.config.ftp.properties.FtpProperties;
import com.example.webapplication.dto.ftp.BreadcrumbDto;
import com.example.webapplication.dto.ftp.FtpEntryDto;
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

    public List<FtpEntryDto> list(String path) {
        return ftpRemoteFileTemplate.execute(session -> {
            FTPFile[] files = session.list(path);
            return Arrays.stream(files)
                    .map(f -> FtpEntryDto.builder()
                            .name(f.getName())
                            .path(path + "/" + f.getName())
                            .directory(f.isDirectory())
                            .size(f.getSize())
                            .modified(f.getTimestamp().toInstant())
                            .build()
                    )
                    .sorted(Comparator
                    .comparing(FtpEntryDto::isDirectory).reversed()
                    .thenComparing(FtpEntryDto::getName))
                    .toList();
        });
    }

    public List<BreadcrumbDto> breadcrumbs(String path) {

        List<BreadcrumbDto> crumbs = new ArrayList<>();

        crumbs.add(new BreadcrumbDto("root", "/"));

        if ("/".equals(path)) return crumbs;

        String[] parts = path.split("/");
        StringBuilder current = new StringBuilder();

        for (String p : parts) {

            if (p.isBlank()) continue;

            current.append("/").append(p);
            crumbs.add(new BreadcrumbDto(p, current.toString()));
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

    public InputStream getLargeFile() {

        try {
            Path filePath = Path.of("/data/files/large-file.zip");

            return Files.newInputStream(filePath);

        } catch (IOException e) {
            throw new RuntimeException("Could not read file", e);
        }
    }

    public Path getFile(String relativePath) {
        // Optional: Basis-Verzeichnis, damit keine beliebigen Pfade gelesen werden können
        Path baseDir = Path.of(ftpProperties.getRemoteDirectory());

        // Sicherstellen, dass kein Pfad-Traversal möglich ist
        Path path = baseDir.resolve(relativePath).normalize();

        if (!path.startsWith(baseDir)) {
            throw new IllegalArgumentException("Ungültiger Pfad");
        }

        return path;
    }
}
