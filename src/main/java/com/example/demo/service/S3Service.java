package com.example.demo.service;

import com.example.demo.Response.FileUploadResponse;
import com.example.demo.entity.UploadedFile;
import com.example.demo.repository.UploadedFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.Instant;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final UploadedFileRepository uploadedFileRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client, UploadedFileRepository uploadedFileRepository) {
        this.s3Client = s3Client;
        this.uploadedFileRepository = uploadedFileRepository;
    }

    public FileUploadResponse uploadToS3(String keyName, MultipartFile file) throws IOException {
        String fileUrl = "";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3Client.putObject(request,RequestBody.fromBytes(file.getBytes()));
        fileUrl =  "https://" + bucketName + ".s3.amazonaws.com/" + keyName;
        UploadedFile uploadedFile = UploadedFile.builder()
                .fileName(keyName)
                .s3Url(fileUrl)
                .fileSize(file.getSize())
                .uploadedAt(Instant.now())
                .build();
        uploadedFileRepository.save(uploadedFile);

FileUploadResponse res = FileUploadResponse.builder()
        .fileName(uploadedFile.getFileName())
        .filePath(uploadedFile.getS3Url())
        .fileSize(uploadedFile.getFileSize())
        .uploadedAt(uploadedFile.getUploadedAt())
        .build();
    return res;
    }

}
