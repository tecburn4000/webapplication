package com.example.webapplication.dto;

import lombok.Data;

/**
 * File information DTO
 */
@Data
public class FileInfo {
    private String name;
    private boolean isDirectory;
    private long size;
    private long modifiedTime;
    private String permissions;
}