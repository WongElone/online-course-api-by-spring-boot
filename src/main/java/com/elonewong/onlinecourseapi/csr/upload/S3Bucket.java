package com.elonewong.onlinecourseapi.csr.upload;

import java.nio.ByteBuffer;

public interface S3Bucket {

    String getBucketName();

    boolean isForPublicRead();

    Upload uploadOneObject(String dirOfTargetFolder, String fileName, String fileExtension, ByteBuffer fileByteBuffer);

    String generateObjectURLString(String keyName);

}
