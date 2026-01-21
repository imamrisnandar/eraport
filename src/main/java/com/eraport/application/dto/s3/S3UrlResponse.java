package com.eraport.application.dto.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3UrlResponse {
    private String key;
    private String url;
    private Long expiresInSeconds;
}
