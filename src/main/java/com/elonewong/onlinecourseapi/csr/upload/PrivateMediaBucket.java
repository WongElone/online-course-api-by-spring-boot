package com.elonewong.onlinecourseapi.csr.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.UUID;

@Component
public class PrivateMediaBucket implements S3Bucket {

    @Value("${core.upload.privateMediaBucket.bucketName}")
    private String bucketName;

    @Value("${core.upload.privateMediaBucket.isPublicReadBucket}")
    private String isForPublicRead;

    @Value("${core.upload.privateMediaBucket.presignedUrlExpireTimeInMinutes}")
    private Integer presignedUrlExpireTimeInMinutes;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    @Autowired
    private UploadRepository uploadRepository;

    @Override
    public String getBucketName() {
        return bucketName;
    }

    @Override
    public boolean isForPublicRead() {
        return "true".equals(isForPublicRead);
    }

    @Override
    public Upload uploadOneObject(String dirOfTargetFolder, String fileName, String fileExtension, ByteBuffer fileByteBuffer) {
        String objKey = this.generateObjectKey(dirOfTargetFolder, fileName, fileExtension);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(this.bucketName)
                .key(objKey)
                .acl(ObjectCannedACL.PRIVATE)
                .build();

        s3Client.putObject(objectRequest, RequestBody.fromByteBuffer(fileByteBuffer));

        Upload newUpload = Upload.builder()
                .fileName(fileName)
                .fileExtension(fileExtension)
                .s3ObjectKey(objKey)
                .s3BucketEnum(Upload.S3BucketEnum.PrivateMedia)
                .toBeRemovedFromS3(false)
                .build();
        return uploadRepository.save(newUpload);
    }

    @Override
    public String generateObjectURLString(String objKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(this.bucketName)
                .key(objKey)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(this.presignedUrlExpireTimeInMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = this.s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    private String generateObjectKey(String parentDir, String filename, String fileExtension) {
        return parentDir + filename + UUID.randomUUID() + "." + fileExtension;
    }

}
