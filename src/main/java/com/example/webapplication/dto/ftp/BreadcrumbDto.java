package com.example.webapplication.dto.ftp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class BreadcrumbDto {
    private String name;
    private String path;
}

