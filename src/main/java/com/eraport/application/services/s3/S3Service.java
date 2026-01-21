package com.eraport.application.services.s3;

import com.eraport.application.dto.s3.S3ConnectionTestResponse;
import com.eraport.application.dto.s3.S3FileResponse;
import com.eraport.application.dto.s3.S3ListResponse;
import com.eraport.application.dto.s3.S3UrlResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    /**
     * Upload a file to S3
     * 
     * @param file   The file to upload
     * @param folder Optional folder/prefix for organizing files
     * @return File metadata response
     */
    S3FileResponse uploadFile(MultipartFile file, String folder);

    /**
     * Download a file from S3
     * 
     * @param key The S3 object key
     * @return Resource containing the file data
     */
    Resource downloadFile(String key);

    /**
     * Delete a file from S3
     * 
     * @param key The S3 object key
     */
    void deleteFile(String key);

    /**
     * List files in S3 with optional prefix
     * 
     * @param prefix Optional prefix to filter files
     * @return List of files
     */
    S3ListResponse listFiles(String prefix);

    /**
     * Generate a presigned URL for temporary file access
     * 
     * @param key               The S3 object key
     * @param expirationMinutes URL expiration time in minutes
     * @return Presigned URL response
     */
    S3UrlResponse getPresignedUrl(String key, Integer expirationMinutes);

    /**
     * Get file metadata
     * 
     * @param key The S3 object key
     * @return File metadata
     */
    S3FileResponse getFileMetadata(String key);

    /**
     * Test S3 connection and bucket access
     * 
     * @return Connection test result
     */
    S3ConnectionTestResponse testConnection();
}
