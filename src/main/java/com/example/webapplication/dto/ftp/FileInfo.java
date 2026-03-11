package com.example.webapplication.dto.ftp;

import lombok.Builder;
import lombok.Data;

/**
 * File information DTO
 */
@Builder
@Data
public class FileInfo {
    private String name;
    private boolean isDirectory;
    private long size;
//    private long modifiedTime;
//    private String permissions;
}