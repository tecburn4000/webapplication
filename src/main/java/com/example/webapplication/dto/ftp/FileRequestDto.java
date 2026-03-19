package com.example.webapplication.dto.ftp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileRequestDto {
    private String filePath;

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}