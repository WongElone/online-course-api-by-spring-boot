package com.elonewong.onlinecourseapi.csr.chapter;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntityRepository;
import com.elonewong.onlinecourseapi.csr.custom.CanGetEntityManager;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChapterRepository extends BaseUuidEntityRepository<Chapter>, CanGetEntityManager {

    @Query("SELECT COUNT(c) FROM Chapter c WHERE id in ?1")
    Integer countByIds(List<String> ids);

}
