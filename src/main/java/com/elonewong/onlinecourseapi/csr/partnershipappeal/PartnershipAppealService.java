package com.elonewong.onlinecourseapi.csr.partnershipappeal;

import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.CourseRepository;
import com.elonewong.onlinecourseapi.csr.course.CourseService;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;
import com.elonewong.onlinecourseapi.csr.teacher.TeacherService;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.exception.AuthorizationException;
import com.elonewong.onlinecourseapi.exception.BadRequestException;
import com.elonewong.onlinecourseapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartnershipAppealService {

    private PartnershipAppealRepository partnershipAppealRepository;
    private CourseRepository courseRepository;
    private PartnershipAppealResponseMapper partnershipAppealResponseMapper;
    private CourseService courseService;

    @Autowired
    public PartnershipAppealService(PartnershipAppealRepository partnershipAppealRepository, CourseRepository courseRepository, PartnershipAppealResponseMapper partnershipAppealResponseMapper, CourseService courseService, TeacherService teacherService) {
        this.partnershipAppealRepository = partnershipAppealRepository;
        this.courseRepository = courseRepository;
        this.partnershipAppealResponseMapper = partnershipAppealResponseMapper;
        this.courseService = courseService;
        this.teacherService = teacherService;
    }

    private TeacherService teacherService;

    public PartnershipAppealResponse addOnePartnershipAppeal(PartnershipAppealRequest partnershipAppealRequest, Profile userProfile) {
        String courseId = partnershipAppealRequest.courseId();
        Course course = courseService.findCourseById(courseId);
        if (courseService.isTeacherOfTheCourse(userProfile, course)) {
            throw new BadRequestException("You are already one of the teachers of the course");
        }

        PartnershipAppeal newAppeal = PartnershipAppeal.builder()
                .teacher(teacherService.findTeacherById(userProfile.profileTableId()))
                .course(course)
                .build();

        return partnershipAppealResponseMapper.apply(
            partnershipAppealRepository.save(newAppeal)
        );
    }

    public List<PartnershipAppealResponse> getAllPartnershipAppealsOfOneCourse(String courseId, Profile userProfile) throws AuthorizationException {
        courseService.validateIsTeacherOfTheCourse(userProfile, courseId);

        return partnershipAppealRepository.findAllAppealsOfOneCourse(courseId).stream()
                .map(partnershipAppealResponseMapper).collect(Collectors.toList());
    }

    public List<PartnershipAppealResponse> getAllPartnershipAppealsOfOneTeacher(Profile userProfile) throws AuthorizationException {
        if (!Role.TEACHER.equals(userProfile.role())) {
            throw new AuthorizationException("The user is not a teacher");
        }

        return partnershipAppealRepository.findAllAppealsOfOneTeacher(userProfile.profileTableId()).stream()
                .map(partnershipAppealResponseMapper).collect(Collectors.toList());
    }

    @Transactional
    public void approveOnePartnershipAppeal(PartnershipAppealCompositeIdRequest appealCompositeIdRequest, Profile userProfile) throws AuthorizationException {
        Course course = courseService.findCourseById(appealCompositeIdRequest.courseId());
        courseService.validateIsTeacherOfTheCourse(userProfile, course);
        Teacher joinTeacher = teacherService.findTeacherById(appealCompositeIdRequest.joinTeacherId());

        PartnershipAppealCompositeId appealCompositeId = new PartnershipAppealCompositeId(joinTeacher, course);
        PartnershipAppeal appeal = this.findOnePartnershipAppealById(appealCompositeId);

        List<Teacher> courseTeachers = course.getTeachers();
        courseTeachers.add(joinTeacher);
        course.setTeachers(courseTeachers);

        courseRepository.save(course);
        partnershipAppealRepository.delete(appeal);
    }

    public PartnershipAppeal findOnePartnershipAppealById(PartnershipAppealCompositeId compositeId) {
        return partnershipAppealRepository.findById(compositeId)
                .orElseThrow(() -> new ResourceNotFoundException("Appeal to join course of provided courseId by teacher of provided teacherId not found"));
    }

    public void deleteOnePartnershipAppeal(PartnershipAppealCompositeIdRequest appealCompositeIdRequest, Profile userProfile) throws AuthorizationException {
        Course course = courseService.findCourseById(appealCompositeIdRequest.courseId());
        courseService.validateIsTeacherOfTheCourse(userProfile, course);
        Teacher joinTeacher = teacherService.findTeacherById(appealCompositeIdRequest.joinTeacherId());

        PartnershipAppealCompositeId appealCompositeId = new PartnershipAppealCompositeId(joinTeacher, course);
        PartnershipAppeal appeal = this.findOnePartnershipAppealById(appealCompositeId);

        partnershipAppealRepository.delete(appeal);
    }
}
