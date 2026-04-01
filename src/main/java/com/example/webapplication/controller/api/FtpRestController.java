package com.example.webapplication.controller.api;

import com.example.webapplication.dto.ftp.FileRequestDto;
import com.example.webapplication.dto.ftp.ListFileRequestDto;
import com.example.webapplication.exception.ftp.FtpException;
import com.example.webapplication.security.permissions.PermissionFtp;
import com.example.webapplication.security.permissions.PermissionRestApi;
import com.example.webapplication.service.ftp.FtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;

@Slf4j
@FtpException
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FtpRestController {

    private final FtpService ftpService;
    private final MethodValidationPostProcessor methodValidationPostProcessor;

    @PermissionRestApi
    @PostMapping(value = "/stream-zip", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> streamZip(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ListFileRequestDto listFileRequestDto) {

        if (log.isDebugEnabled()) {
            logJwt(jwt);
        }

        if (!checkIfFilesExists(listFileRequestDto)) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody stream = ftpService.createZipStream(listFileRequestDto);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=files.zip")
                .body(stream);
    }

    private boolean checkIfFilesExists(ListFileRequestDto listFileRequestDto) {
        for (String file : listFileRequestDto.getFilePaths()) {
            if (!ftpService.fileExists(file)) {
                return false;
            }
        }
        return true;
    }

    @PermissionRestApi
    @GetMapping("/test")
    public String test(@AuthenticationPrincipal Jwt jwt) {

        if (log.isDebugEnabled()) {
            logJwt(jwt);
        }

        return "Test " + jwt.getSubject();
    }

    private static void logJwt(Jwt jwt) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : jwt.getClaims().entrySet()) {
            builder.append("\t").append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
        }
        log.debug("\nJWT Claims:\n{}", builder);
    }
}

