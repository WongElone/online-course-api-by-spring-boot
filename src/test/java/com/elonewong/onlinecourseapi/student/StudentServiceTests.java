package com.elonewong.onlinecourseapi.student;

import com.elonewong.onlinecourseapi.csr.student.*;
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
public class StudentServiceTests {

    @Mock
    private StudentRepository studentRepository;
    @Spy
    private StudentResponseMapper studentResponseMapper;
    @InjectMocks
    private StudentService studentService;

    private Student student1;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        User user1 = User.builder()
                .id("user-01")
                .role(Role.STUDENT)
                .email("john.doe02@example.com")
                .password("password")
                .build();
        student1 = Student.builder()
                .id("student-01")
                .user(user1)
                .courses(List.of())
                .build();
    }

    @Test
    public void test_StudentService_GetOneStudent_ReturnsCorrectStudentResponse() {
        when(studentRepository.findById(student1.getId())).thenReturn(Optional.ofNullable(student1));

        StudentResponse studentResponse = studentService.getOneStudent(student1.getId());

        Assertions.assertThat(studentResponse).isEqualTo(
                studentResponseMapper.apply(student1)
        );
    }

    @Test
    public void test_StudentService_FindStudentById_ReturnsCorrectStudent() {
        when(studentRepository.findById(student1.getId())).thenReturn(Optional.ofNullable(student1));

        Student student = studentService.findStudentById(student1.getId());

        Assertions.assertThat(student).isEqualTo(student1);
    }

}
