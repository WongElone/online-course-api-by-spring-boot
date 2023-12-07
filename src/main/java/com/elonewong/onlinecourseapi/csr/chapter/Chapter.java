package com.elonewong.onlinecourseapi.csr.chapter;

import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntity;
import com.elonewong.onlinecourseapi.csr.lesson.Lesson;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@SuperBuilder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Chapter extends BaseUuidEntity {

    @ManyToOne(
        cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST
        },
        optional = false
    )
    @ToString.Include
    private Course course;

    @Min(1)
    @ToString.Include
    private Integer orderInCourse;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.ALL
            },
            mappedBy = "chapter"
//            ,orphanRemoval = true
    )
    @OrderBy("orderInChapter")
    private List<Lesson> lessons;

    @NotBlank
    @Size(max = 255)
    @ToString.Include
    private String title;

    public record SimpleChapter(
        String chapterId,
        SimpleCourse course,
        Integer orderInCourse,
        String title
    ) {}

}
