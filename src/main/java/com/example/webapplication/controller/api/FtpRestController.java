package com.example.webapplication.controller.api;

import com.example.webapplication.dto.ftp.FileRequestDto;
import com.example.webapplication.exception.ftp.FtpException;
import com.example.webapplication.security.permissions.PermissionFtp;
import com.example.webapplication.service.ftp.FtpService;
import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@FtpException
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FtpRestController {

    private final FtpService ftpService;

    @PermissionFtp
    @GetMapping("/stream")
    public ResponseEntity<InputStreamResource> streamFile() {

        InputStream stream = ftpService.getLargeFile();

        InputStreamResource resource = new InputStreamResource(stream);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"large-file.zip\"")
                .body(resource);
    }

    @PermissionFtp
    @PostMapping("/stream")
    public ResponseEntity<InputStreamResource> streamFile(@RequestBody FileRequestDto request) throws IOException {

        Path path = ftpService.getFile(request.getFilePath());

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

        return ResponseEntity.ok()
                .contentLength(Files.size(path))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + path.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/hello")
    public String hello(@AuthenticationPrincipal Jwt jwt) {
        return "Hello " + jwt.getSubject();
    }
}
