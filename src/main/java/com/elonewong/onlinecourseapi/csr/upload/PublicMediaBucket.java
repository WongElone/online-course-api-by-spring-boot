package com.elonewong.onlinecourseapi.csr.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.nio.ByteBuffer;
import java.util.UUID;

@Component
public class PublicMediaBucket implements S3Bucket {

    @Value("${core.upload.publicMediaBucket.bucketName}")
    private String bucketName;

    @Value("${core.upload.publicMediaBucket.isPublicReadBucket}")
    private String isForPublicRead;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    @Autowired
    private Region region;

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

//    /**
//     *
//     * @param parentDir
//     * @param filename
//     * @param extension
//     * @return new keyName
//     */
//    private String adjustKeyNameToHandleDuplications(String parentDir, String filename, String extension) {
//        boolean shouldAppend = false;
//        String[] s = filename.split("_-_");
//        if (s.length > 1) {
//            try {
//                int duplications = Integer.parseInt(s[s.length - 1]);
//                s[s.length - 1] = String.valueOf(duplications + 1);
//                return parentDir + String.join("", s) + extension;
//            } catch (NumberFormatException e) {
//                shouldAppend = true;
//            }
//        }
//        if (shouldAppend) {
//            return parentDir + filename + "_-_1" + extension;
//        }
//    }

    @Override
    public Upload uploadOneObject(String dirOfTargetFolder, String fileName, String fileExtension, ByteBuffer fileByteBuffer) {
        String objKey = this.generateObjectKey(dirOfTargetFolder, fileName, fileExtension);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(this.bucketName)
                .key(objKey)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(objectRequest, RequestBody.fromByteBuffer(fileByteBuffer));

        Upload newUpload = Upload.builder()
                .fileName(fileName)
                .fileExtension(fileExtension)
                .s3ObjectKey(objKey)
                .s3BucketEnum(Upload.S3BucketEnum.PublicMedia)
                .toBeRemovedFromS3(false)
                .build();
        return uploadRepository.save(newUpload);
    }

    @Override
    public String generateObjectURLString(String objKey) {
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(this.bucketName)
                .key(objKey)
                .region(this.region)
                .build();

        return s3Client.utilities().getUrl(getUrlRequest).toExternalForm();
    }

    private String generateObjectKey(String parentDir, String filename, String fileExtension) {
        return parentDir + filename + UUID.randomUUID() + "." + fileExtension;
    }

}
