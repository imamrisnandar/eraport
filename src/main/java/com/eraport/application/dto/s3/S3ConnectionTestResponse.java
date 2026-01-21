package com.eraport.application.dto.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3ConnectionTestResponse {
    private Boolean connected;
    private String message;
    private String bucketName;
    private String endpoint;
    private String region;
}
