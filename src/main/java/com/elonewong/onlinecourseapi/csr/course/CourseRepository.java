package com.elonewong.onlinecourseapi.csr.course;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CourseRepository extends BaseUuidEntityRepository<Course> {

    @Query(value = "SELECT c from Course c WHERE c.id IN :Ids") // capital letter Course to reference the class Course because we are using JPQL,
    List<Course> findAllCoursesByIds(Collection<String> Ids);

    @Query(value = "SELECT COUNT(*) FROM students_courses sc WHERE sc.course_id = ?1", nativeQuery = true)
    Integer findStudentsCountOfTheCourse(String courseId);

}
