package com.ecommerce.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final S3Client r2Client;

    @Value("${r2.bucket.name}")
    private String bucketName;

    @Value("${r2.public.url}")
    private String publicUrl;

    @Override
    public String uploadFile(byte[] data, String fileName, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .contentLength((long) data.length)
                    .build();

            r2Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
            
            String fileUrl = publicUrl + "/" + fileName;
            log.info("File uploaded successfully: {}", fileUrl);
            
            return fileUrl;
            
        } catch (Exception e) {
            log.error("Failed to upload file: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] downloadFile(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            ResponseInputStream<GetObjectResponse> response = r2Client.getObject(getObjectRequest);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = response.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            
            log.info("File downloaded successfully: {}", fileUrl);
            return baos.toByteArray();
            
        } catch (IOException e) {
            log.error("Failed to download file {}: {}", fileUrl, e.getMessage());
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            
            r2Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully: {}", fileUrl);
            
            return true;
            
        } catch (Exception e) {
            log.error("Failed to delete file {}: {}", fileUrl, e.getMessage());
            return false;
        }
    }

    private String extractFileNameFromUrl(String fileUrl) {
        // Extract filename from URL: https://your-bucket.r2.dev/folder/filename.pdf
        if (fileUrl.startsWith(publicUrl)) {
            return fileUrl.replace(publicUrl + "/", "");
        }
        // If it's already just a path, return as is
        return fileUrl;
    }
}
