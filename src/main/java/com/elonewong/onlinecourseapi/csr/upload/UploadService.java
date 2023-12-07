package com.elonewong.onlinecourseapi.csr.upload;

import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.CourseService;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import com.elonewong.onlinecourseapi.exception.AuthorizationException;
import com.elonewong.onlinecourseapi.exception.BadRequestException;
import com.elonewong.onlinecourseapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {

    private UploadRepository uploadRepository;
    private UploadResponseGenerator uploadResponseGenerator;
    private PublicMediaBucket publicMediaBucket;
    private PrivateMediaBucket privateMediaBucket;
    private CourseService courseService;

    @Autowired
    public UploadService(UploadRepository uploadRepository, UploadResponseGenerator uploadResponseGenerator, PublicMediaBucket publicMediaBucket, PrivateMediaBucket privateMediaBucket, CourseService courseService) {
        this.uploadRepository = uploadRepository;
        this.uploadResponseGenerator = uploadResponseGenerator;
        this.publicMediaBucket = publicMediaBucket;
        this.privateMediaBucket = privateMediaBucket;
        this.courseService = courseService;
    }

    /**
     *
     * @param file
     * @param bucketEnum
     * @param dirOfTargetFolder
     * @return Upload Id
     */
    public String uploadOneObject(MultipartFile file, Upload.S3BucketEnum bucketEnum, String dirOfTargetFolder) {
        S3Bucket s3Bucket = this.grabBucket(bucketEnum);

        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null : "Filename of the uploading multipart file is null";
        String[] s = originalFilename.split("\\.");
        assert s.length > 1 : "Invalid filename of the uploading multipart file";
        String fileExtension = s[s.length - 1];
        String fileName = String.join(".", Arrays.stream(s).limit(s.length - 1).toArray(String[]::new));

        try {
            ByteBuffer fileByteBuffer = ByteBuffer.wrap(file.getBytes());
            Upload upload = s3Bucket.uploadOneObject(dirOfTargetFolder, fileName, fileExtension, fileByteBuffer);
            return upload.getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public UploadResponse getOneUpload(String uploadId, Profile userProfile) throws AuthorizationException {
        Upload upload = this.findOneUploadById(uploadId);
        if (upload.getAssignment() != null) {
            Course course = upload.getAssignment().getCourse();
            courseService.validateHavingAccessToCourse(userProfile, course);
        }
        String urlStringOfOneUpload = this.getURLStringOfOneUpload(upload);
        return uploadResponseGenerator.generate(upload, urlStringOfOneUpload);
    }

    private String getURLStringOfOneUpload(Upload upload) {
        S3Bucket s3Bucket = this.grabBucket(upload.getS3BucketEnum());
        return s3Bucket.generateObjectURLString(upload.getS3ObjectKey());
    }

    public Upload findOneUploadById(String uploadId) {
        Upload upload = uploadRepository.findById(uploadId).orElseThrow(() -> new ResourceNotFoundException("Upload of provided id " + uploadId + " not found"));
        if (upload.getToBeRemovedFromS3()) {
            throw new BadRequestException("This upload is scheduled to be removed in the future");
        }
        return upload;
    }

    public List<Upload> findUploadsByIds(List<String> uploadIds) {
        List<Upload> uploads = uploadRepository.findAllById(uploadIds);
        assert (uploads.size() == uploadIds.size()) : new BadRequestException("Some Upload ids not found");
        for (Upload upload : uploads) {
            if (upload.getToBeRemovedFromS3()) throw new BadRequestException("Upload of id " + upload.getId() + " is scheduled to be removed");
        }
        return uploads;
    }

    private S3Bucket grabBucket(Upload.S3BucketEnum bucketEnum) {
        if (bucketEnum.equals(Upload.S3BucketEnum.PublicMedia)) {
            return publicMediaBucket;
        } else if (bucketEnum.equals(Upload.S3BucketEnum.PrivateMedia)) {
            return privateMediaBucket;
        } else {
            throw new RuntimeException("invalid s3 bucket");
        }
    }

}
