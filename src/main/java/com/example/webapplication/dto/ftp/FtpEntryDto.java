package com.example.webapplication.dto.ftp;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class FtpEntryDto {
    String name;
    String path;
    boolean directory;
    long size;
    Instant modified;
}
