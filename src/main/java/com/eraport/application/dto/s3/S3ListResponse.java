package com.eraport.application.dto.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3ListResponse {
    private String prefix;
    private Integer totalFiles;
    private List<S3FileResponse> files;
}
