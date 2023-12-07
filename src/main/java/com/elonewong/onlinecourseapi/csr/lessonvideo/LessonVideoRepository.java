package com.elonewong.onlinecourseapi.csr.lessonvideo;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntityRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LessonVideoRepository extends BaseUuidEntityRepository<LessonVideo> {

    @Query("SELECT lv from LessonVideo lv WHERE lv.upload.id = ?1")
    Optional<LessonVideo> findByUploadId(String uploadId);

}
