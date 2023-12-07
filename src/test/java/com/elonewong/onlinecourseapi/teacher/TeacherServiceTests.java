package com.elonewong.onlinecourseapi.teacher;

import com.elonewong.onlinecourseapi.csr.teacher.*;
import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.csr.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTests {

    @Mock
    private TeacherRepository teacherRepository;
    @Spy
    private TeacherResponseMapper teacherResponseMapper;
    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher1;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        User user1 = User.builder()
                .id("user-01")
                .role(Role.TEACHER)
                .email("john.doe01@example.com")
                .password("password")
                .build();
        teacher1 = Teacher.builder()
                .id("teacher-01")
                .user(user1)
                .courses(List.of())
                .build();
    }

    @Test
    public void test_TeacherService_GetAllTeachers_ReturnsListOfCorrectSizeAndElements() {
        when(teacherRepository.findAll()).thenReturn(List.of(teacher1));

        List<TeacherResponse> teacherResponses = teacherService.getAllTeachers();

        Assertions.assertThat(teacherResponses).hasSize(1);
        Assertions.assertThat(teacherResponses).containsAll(List.of(
                teacherResponseMapper.apply(teacher1)
        ));
    }

    @Test
    public void test_TeacherService_GetOneTeacher_ReturnsCorrectTeacherResponse() {
        when(teacherRepository.findById(teacher1.getId())).thenReturn(Optional.ofNullable(teacher1));

        TeacherResponse teacherResponse = teacherService.getOneTeacher(teacher1.getId());

        Assertions.assertThat(teacherResponse).isEqualTo(
                teacherResponseMapper.apply(teacher1)
        );
    }

    @Test
    public void test_TeacherService_FindTeacherById_ReturnsCorrectTeacher() {
        when(teacherRepository.findById(teacher1.getId())).thenReturn(Optional.ofNullable(teacher1));

        Teacher teacher = teacherService.findTeacherById(teacher1.getId());

        Assertions.assertThat(teacher).isEqualTo(teacher1);
    }

}
