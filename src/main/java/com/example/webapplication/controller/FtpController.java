package com.example.webapplication.controller;

import com.example.webapplication.exception.ftp.FtpException;
import com.example.webapplication.security.permissions.PermissionFtp;
import com.example.webapplication.service.ftp.FtpService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Path;

@RequiredArgsConstructor
@Controller
@FtpException
@RequestMapping("/ftp")
public class FtpController {

    private final FtpService ftpService;

//    @PermissionFtp
//    @GetMapping("/download")
//    public String download(@RequestParam String filePath) throws IOException {
//        ftpService.downloadToLocalFile(filePath);
//        Path targetDirectory = Path.of(filePath);
//        return "redirect:/ftp/ftp-browserpath=" + targetDirectory;
//    }

    @PermissionFtp
    @GetMapping("/ftp-browser")
    public String browse(@RequestParam(defaultValue = "/") String path, Model model) {
        model.addAttribute("files", ftpService.list(path));
        model.addAttribute("breadcrumbs", ftpService.breadcrumbs(path));
        model.addAttribute("path", path);
        return "ftp/ftp-browser";
    }

    @PermissionFtp
    @GetMapping("/download")
    public String downloadFile(@RequestParam String filePath, HttpServletResponse response) {
        response.setBufferSize(8192);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition","attachment; filename=\"" + filePath + "\"");
        if (!ftpService.fileExists(filePath)) {
            return "redirect:/ftp/ftp-browser";
        }
        ftpService.downloadToBrowser(filePath, response);
        Path targetDirectory = Path.of(filePath);
        return "redirect:/ftp/ftp-browser?path=" + targetDirectory;
    }
}