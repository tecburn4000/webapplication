package com.example.webapplication.service.ftp;

import com.example.webapplication.dto.ftp.BreadcrumbDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for generating breadcrumb navigation for FTP paths.
 * <p>
 * This service converts a given FTP path into a list of {@link BreadcrumbDto}
 * objects representing each level of the directory hierarchy.
 *
 * <p><b>Example:</b>
 * <pre>
 * Input:  /folder/subfolder
 * Output: [
 *   ("root", "/"),
 *   ("folder", "/folder"),
 *   ("subfolder", "/folder/subfolder")
 * ]
 * </pre>
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 *     <li>Parse FTP paths into hierarchical segments</li>
 *     <li>Generate navigation-friendly breadcrumb structures</li>
 *     <li>Ensure a consistent root entry</li>
 * </ul>
 *
 * <p><b>Implementation details:</b>
 * <ul>
 *     <li>Ignores empty path segments (e.g. leading slashes)</li>
 *     <li>Builds paths incrementally using a {@link StringBuilder}</li>
 *     <li>Always includes a root element ("/")</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FtpBreadcrumbService {

    /**
     * Generates breadcrumb navigation entries for a given FTP path.
     * <p>
     * The method converts a path into a hierarchical list of {@link BreadcrumbDto}
     * objects. Each element represents one level of the path and contains both
     * a display name and the corresponding full path.
     *
     * <p><b>Behavior:</b>
     * <ul>
     *     <li>Always includes a root element: {@code ("root", "/")}</li>
     *     <li>Splits the path by {@code "/"} and builds it incrementally</li>
     *     <li>Ignores empty path segments (e.g. caused by multiple slashes)</li>
     * </ul>
     *
     * <p><b>Edge cases:</b>
     * <ul>
     *     <li>If {@code path} is {@code null}, blank, or {@code "/"}, only the root element is returned</li>
     *     <li>Multiple slashes (e.g. {@code "//folder///sub"}) are normalized implicitly</li>
     * </ul>
     *
     * <p><b>Example:</b>
     * <pre>
     * Input:  "/folder/subfolder"
     * Output: [
     *   ("root", "/"),
     *   ("folder", "/folder"),
     *   ("subfolder", "/folder/subfolder")
     * ]
     * </pre>
     *
     * @param path the FTP path (maybe {@code null} or blank)
     * @return a list of {@link BreadcrumbDto} representing the breadcrumb navigation
     */
    public List<BreadcrumbDto> breadcrumbs(String path) {
        List<BreadcrumbDto> crumbs = new ArrayList<>();
        crumbs.add(new BreadcrumbDto("root", "/"));
        if (path == null || path.isBlank() || "/".equals(path)) {
            return crumbs;
        }
        String[] parts = path.split("/");
        StringBuilder current = new StringBuilder();
        for (String p : parts) {
            if (p.isBlank()) {
                continue;
            }
            current.append("/").append(p);
            crumbs.add(new BreadcrumbDto(p, current.toString()));
        }
        return crumbs;
    }
}
