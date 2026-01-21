package com.eraport.application.services.s3;

import com.eraport.application.dto.s3.S3ConnectionTestResponse;
import com.eraport.application.dto.s3.S3FileResponse;
import com.eraport.application.dto.s3.S3ListResponse;
import com.eraport.application.dto.s3.S3UrlResponse;
import com.eraport.infrastructure.config.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Config s3Config;

    @Override
    public S3FileResponse uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = file.getOriginalFilename();
            String key = generateKey(folder, fileName);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            log.info("File uploaded successfully: {}", key);

            return S3FileResponse.builder()
                    .key(key)
                    .fileName(fileName)
                    .url(s3Config.getEndpoint() + "/" + s3Config.getBucketName() + "/" + key)
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .uploadedAt(Instant.now())
                    .build();

        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource downloadFile(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

            log.info("File downloaded successfully: {}", key);

            return new ByteArrayResource(objectBytes.asByteArray());

        } catch (S3Exception e) {
            log.error("Error downloading file from S3: {}", key, e);
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            log.info("File deleted successfully: {}", key);

        } catch (S3Exception e) {
            log.error("Error deleting file from S3: {}", key, e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public S3ListResponse listFiles(String prefix) {
        try {
            ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                    .bucket(s3Config.getBucketName());

            if (prefix != null && !prefix.isEmpty()) {
                requestBuilder.prefix(prefix);
            }

            ListObjectsV2Response response = s3Client.listObjectsV2(requestBuilder.build());

            List<S3FileResponse> files = new ArrayList<>();
            for (S3Object s3Object : response.contents()) {
                files.add(S3FileResponse.builder()
                        .key(s3Object.key())
                        .fileName(extractFileName(s3Object.key()))
                        .url(s3Config.getEndpoint() + "/" + s3Config.getBucketName() + "/" + s3Object.key())
                        .size(s3Object.size())
                        .uploadedAt(s3Object.lastModified())
                        .build());
            }

            log.info("Listed {} files with prefix: {}", files.size(), prefix);

            return S3ListResponse.builder()
                    .prefix(prefix)
                    .totalFiles(files.size())
                    .files(files)
                    .build();

        } catch (S3Exception e) {
            log.error("Error listing files from S3", e);
            throw new RuntimeException("Failed to list files: " + e.getMessage(), e);
        }
    }

    @Override
    public S3UrlResponse getPresignedUrl(String key, Integer expirationMinutes) {
        try {
            int expiration = (expirationMinutes != null && expirationMinutes > 0) ? expirationMinutes : 60;

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expiration))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            log.info("Generated presigned URL for: {}", key);

            return S3UrlResponse.builder()
                    .key(key)
                    .url(presignedRequest.url().toString())
                    .expiresInSeconds((long) (expiration * 60))
                    .build();

        } catch (S3Exception e) {
            log.error("Error generating presigned URL for: {}", key, e);
            throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage(), e);
        }
    }

    @Override
    public S3FileResponse getFileMetadata(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(key)
                    .build();

            HeadObjectResponse metadata = s3Client.headObject(headObjectRequest);

            log.info("Retrieved metadata for: {}", key);

            return S3FileResponse.builder()
                    .key(key)
                    .fileName(extractFileName(key))
                    .url(s3Config.getEndpoint() + "/" + s3Config.getBucketName() + "/" + key)
                    .size(metadata.contentLength())
                    .contentType(metadata.contentType())
                    .uploadedAt(metadata.lastModified())
                    .build();

        } catch (S3Exception e) {
            log.error("Error getting file metadata from S3: {}", key, e);
            throw new RuntimeException("Failed to get file metadata: " + e.getMessage(), e);
        }
    }

    @Override
    public S3ConnectionTestResponse testConnection() {
        try {
            // Try to check if bucket exists and is accessible
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .build();

            s3Client.headBucket(headBucketRequest);

            log.info("S3 connection test successful for bucket: {}", s3Config.getBucketName());

            return S3ConnectionTestResponse.builder()
                    .connected(true)
                    .message("Successfully connected to S3 bucket")
                    .bucketName(s3Config.getBucketName())
                    .endpoint(s3Config.getEndpoint())
                    .region(s3Config.getRegion())
                    .build();

        } catch (S3Exception e) {
            log.error("S3 connection test failed", e);

            String errorMessage;
            if (e.statusCode() == 404) {
                errorMessage = "Bucket not found: " + s3Config.getBucketName();
            } else if (e.statusCode() == 403) {
                errorMessage = "Access denied. Check credentials and bucket permissions";
            } else {
                errorMessage = "Connection failed: " + e.getMessage();
            }

            return S3ConnectionTestResponse.builder()
                    .connected(false)
                    .message(errorMessage)
                    .bucketName(s3Config.getBucketName())
                    .endpoint(s3Config.getEndpoint())
                    .region(s3Config.getRegion())
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error during S3 connection test", e);

            return S3ConnectionTestResponse.builder()
                    .connected(false)
                    .message("Unexpected error: " + e.getMessage())
                    .bucketName(s3Config.getBucketName())
                    .endpoint(s3Config.getEndpoint())
                    .region(s3Config.getRegion())
                    .build();
        }
    }

    private String generateKey(String folder, String fileName) {
        String uniqueId = UUID.randomUUID().toString();
        String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        if (folder != null && !folder.isEmpty()) {
            return folder + "/" + uniqueId + "_" + sanitizedFileName;
        }
        return uniqueId + "_" + sanitizedFileName;
    }

    private String extractFileName(String key) {
        int lastSlashIndex = key.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < key.length() - 1) {
            return key.substring(lastSlashIndex + 1);
        }
        return key;
    }
}
