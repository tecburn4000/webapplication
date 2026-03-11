package com.example.webapplication.dto.ftp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Breadcrumb {
    private String name;
    private String path;
}
