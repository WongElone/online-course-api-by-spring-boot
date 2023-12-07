package com.elonewong.onlinecourseapi.csr.enrollment;

import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.student.Student;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment extends EnrollmentAuditable {

    @ManyToOne(
            cascade = {
                    CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.PERSIST
            },
            optional = false
    )
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Student student;

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

    private Instant dropAt;

    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "payment_amount")),
            @AttributeOverride(name = "type", column = @Column(name = "payment_type")),
            @AttributeOverride(name = "description", column = @Column(name = "payment_description"))
    })
    private EnrollmentPayment payment;

}
