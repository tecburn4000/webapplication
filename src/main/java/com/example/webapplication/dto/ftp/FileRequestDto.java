package com.example.webapplication.dto.ftp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileRequestDto {
    private String filePath;
}
