package com.elonewong.onlinecourseapi.csr.assignment;

import com.elonewong.onlinecourseapi.csr.assignmentattachment.AssignmentAttachment;
import com.elonewong.onlinecourseapi.csr.course.Course;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class Assignment extends AssignmentAuditable {

    @ManyToOne(
            cascade = {
                    CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.PERSIST
            },
            optional = false
    )
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @Size(max = 255)
    @NotBlank
    @ToString.Include
    private String title;

    private String wysiwyg;

    @NotNull
    private Boolean visibleToStudent;

    @NotNull
    private Boolean allowSubmission;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "assignment"
    )
    private List<AssignmentAttachment> attachments;

}
