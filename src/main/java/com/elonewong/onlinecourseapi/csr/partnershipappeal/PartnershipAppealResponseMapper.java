package com.elonewong.onlinecourseapi.csr.partnershipappeal;

import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;
import com.elonewong.onlinecourseapi.csr.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PartnershipAppealResponseMapper implements Function<PartnershipAppeal, PartnershipAppealResponse> {

    @Override
    public PartnershipAppealResponse apply(PartnershipAppeal partnershipAppeal) {
        User user = partnershipAppeal.getTeacher().getUser();
        return new PartnershipAppealResponse(
            new Teacher.SimpleTeacher(
                partnershipAppeal.getTeacher().getId(),
                new User.SimpleUser(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole()
                )
            ),
            new SimpleCourse(
                partnershipAppeal.getCourse().getId(),
                partnershipAppeal.getCourse().getTitle()
            ),
            partnershipAppeal.getAppealedAt()
        );
    }

}
