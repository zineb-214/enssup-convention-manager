package com.zineb.autheapp.Service.impl;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class MinioService {
    @Value("${minio.bucket-name}")
    private String bucket;
    private final MinioClient minioClient;
    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void uploadPdfToMinio(byte[] pdfBytes, String fileName) {
        try {
            InputStream inputStream = new ByteArrayInputStream(pdfBytes);


            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .stream(inputStream, pdfBytes.length, -1)
                    .contentType("application/pdf")
                    .build());

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload vers MinIO : " + e.getMessage(), e);
        }
    }

}
