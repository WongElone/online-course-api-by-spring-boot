package com.elonewong.onlinecourseapi.csr.lesson;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntityRepository;
import com.elonewong.onlinecourseapi.csr.custom.CanGetEntityManager;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LessonRepository extends BaseUuidEntityRepository<Lesson>, CanGetEntityManager {

    @Query("SELECT COUNT(l) FROM Lesson l WHERE id in ?1")
    Integer countByIds(List<String> ids);

}
