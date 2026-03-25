package com.example.webapplication.dto.ftp;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListFileRequestDto {
    private List<String> filePaths;
}