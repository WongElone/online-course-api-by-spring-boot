package com.elonewong.onlinecourseapi.csr.category;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CategoryRepository extends BaseUuidEntityRepository<Category> {

    @Query(value = "SELECT c FROM Category c WHERE c.id IN ?1")
    List<Category> findAllCategoriesByIds(Collection<String> ids);
}
