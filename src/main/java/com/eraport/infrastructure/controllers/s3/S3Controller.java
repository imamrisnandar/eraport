package com.eraport.infrastructure.controllers.s3;

import com.eraport.application.dto.s3.S3ConnectionTestResponse;
import com.eraport.application.dto.s3.S3FileResponse;
import com.eraport.application.dto.s3.S3ListResponse;
import com.eraport.application.dto.s3.S3UrlResponse;
import com.eraport.application.dto.shared.ApiResponse;
import com.eraport.application.services.s3.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Tag(name = "S3 Storage", description = "S3 file storage management APIs")
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/test-connection")
    @Operation(summary = "Test S3 connection", description = "Test connection to S3 bucket and verify configuration")
    public ResponseEntity<ApiResponse<S3ConnectionTestResponse>> testConnection() {
        S3ConnectionTestResponse response = s3Service.testConnection();

        if (response.getConnected()) {
            return ResponseEntity.ok(ApiResponse.success("S3 connection successful", response));
        } else {
            return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file to S3", description = "Upload a file to S3 bucket with optional folder organization")
    public ResponseEntity<ApiResponse<S3FileResponse>> uploadFile(
            @Parameter(description = "File to upload", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "Optional folder/prefix for organizing files") @RequestParam(value = "folder", required = false) String folder) {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        S3FileResponse response = s3Service.uploadFile(file, folder);
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response));
    }

    @GetMapping("/download/{key}")
    @Operation(summary = "Download file from S3", description = "Download a file from S3 bucket by its key")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "S3 object key", required = true) @PathVariable String key) {

        Resource resource = s3Service.downloadFile(key);
        S3FileResponse metadata = s3Service.getFileMetadata(key);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType() != null ? metadata.getContentType()
                        : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{key}")
    @Operation(summary = "Delete file from S3", description = "Delete a file from S3 bucket by its key")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @Parameter(description = "S3 object key", required = true) @PathVariable String key) {

        s3Service.deleteFile(key);
        return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
    }

    @GetMapping("/list")
    @Operation(summary = "List files in S3", description = "List all files in S3 bucket with optional prefix filter")
    public ResponseEntity<ApiResponse<S3ListResponse>> listFiles(
            @Parameter(description = "Optional prefix to filter files") @RequestParam(value = "prefix", required = false) String prefix) {

        S3ListResponse response = s3Service.listFiles(prefix);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/url/{key}")
    @Operation(summary = "Get presigned URL", description = "Generate a temporary presigned URL for file access")
    public ResponseEntity<ApiResponse<S3UrlResponse>> getPresignedUrl(
            @Parameter(description = "S3 object key", required = true) @PathVariable String key,
            @Parameter(description = "URL expiration time in minutes (default: 60)") @RequestParam(value = "expirationMinutes", required = false) Integer expirationMinutes) {

        S3UrlResponse response = s3Service.getPresignedUrl(key, expirationMinutes);
        return ResponseEntity.ok(ApiResponse.success("Presigned URL generated successfully", response));
    }

    @GetMapping("/metadata/{key}")
    @Operation(summary = "Get file metadata", description = "Get metadata information for a file in S3")
    public ResponseEntity<ApiResponse<S3FileResponse>> getFileMetadata(
            @Parameter(description = "S3 object key", required = true) @PathVariable String key) {

        S3FileResponse response = s3Service.getFileMetadata(key);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
