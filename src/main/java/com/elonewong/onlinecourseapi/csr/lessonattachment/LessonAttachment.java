package com.elonewong.onlinecourseapi.csr.lessonattachment;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntity;
import com.elonewong.onlinecourseapi.csr.lesson.Lesson;
import com.elonewong.onlinecourseapi.csr.upload.Upload;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
public class LessonAttachment extends BaseUuidEntity {

    @ManyToOne(
        optional = false
    )
    private Lesson lesson;

    @OneToOne(
        optional = false
    )
    @JoinColumn(name = "upload_id", referencedColumnName = "id")
    private Upload upload;

}
