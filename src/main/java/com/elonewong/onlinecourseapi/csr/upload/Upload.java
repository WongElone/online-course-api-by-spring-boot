package com.elonewong.onlinecourseapi.csr.upload;

import com.elonewong.onlinecourseapi.csr.assignment.Assignment;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class Upload extends UploadAuditable {

    @NotBlank
    @ToString.Include
    private String fileName;

    @NotBlank
    @Size(max = 4)
    private String fileExtension;

    @NotBlank
    @Column(unique = true)
    @ToString.Include
    private String s3ObjectKey;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Upload.S3BucketEnum s3BucketEnum;

//    @ManyToOne
//    private Course course;

    // can be null, which implies it is not a assignment attachment
    @ManyToOne
    private Assignment assignment;

    @ToString.Include
    private Boolean toBeRemovedFromS3;

    @Enumerated(EnumType.STRING)
    private UploadAttachTo attachedTo;

    public enum S3BucketEnum {
        PublicMedia,
        PrivateMedia
    }

    public enum UploadAttachTo {
        LessonVideo,
        LessonAttachment,
        AssignmentAttachment
    }

}
