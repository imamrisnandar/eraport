package com.eraport.application.dto.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3FileResponse {
    private String key;
    private String fileName;
    private String url;
    private Long size;
    private String contentType;
    private Instant uploadedAt;
}
