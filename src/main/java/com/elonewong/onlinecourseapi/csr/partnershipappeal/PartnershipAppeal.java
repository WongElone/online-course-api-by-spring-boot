package com.elonewong.onlinecourseapi.csr.partnershipappeal;

import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PartnershipAppealCompositeId.class)
public class PartnershipAppeal extends PartnershipAppealAuditable {

    @Id
    @ManyToOne(
            cascade = {
                    CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.PERSIST
            },
            optional = false
    )
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private Teacher teacher;

    @Id
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

}
