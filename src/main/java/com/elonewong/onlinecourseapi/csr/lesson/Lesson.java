package com.elonewong.onlinecourseapi.csr.lesson;

import com.elonewong.onlinecourseapi.csr.chapter.Chapter;
import com.elonewong.onlinecourseapi.csr.lessonattachment.LessonAttachment;
import com.elonewong.onlinecourseapi.csr.lessonvideo.LessonVideo;
import com.elonewong.onlinecourseapi.csr.upload.Upload;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Lesson extends LessonAuditable {

    @ManyToOne(
            cascade = {
                    CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.PERSIST
            },
            optional = false
    )
    private Chapter chapter;

    @NotBlank
    @Size(max = 255)
    @ToString.Include
    private String title;

    private String wysiwyg;

    @Min(1)
    @ToString.Include
    private Integer orderInChapter;

    @OneToOne(
        mappedBy = "lesson",
        orphanRemoval = true,
        cascade = CascadeType.ALL
    )
    private LessonVideo video;

    @OneToMany(
        mappedBy = "lesson",
        orphanRemoval = true,
        cascade = CascadeType.ALL
    )
    private List<LessonAttachment> attachments;

    public record SimpleLesson(
        String id,
        String title,
        Integer orderInChapter
    ) {}

}
