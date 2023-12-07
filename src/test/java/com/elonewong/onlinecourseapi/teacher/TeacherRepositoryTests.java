package com.elonewong.onlinecourseapi.teacher;

import com.elonewong.onlinecourseapi.csr.teacher.Teacher;
import com.elonewong.onlinecourseapi.csr.teacher.TeacherRepository;
import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.csr.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TeacherRepositoryTests {

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    public void test_TeacherRepository_FindByUserId_ReturnCorrectTeacher() {

        // Arrange
        List<User> users = new ArrayList<>();
        List<Teacher> teachers = new ArrayList<>();
        for (int i = 0; i < 2; i ++){
            User newUser = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("johndoe0" + i + "@example.com")
                    .role(Role.TEACHER)
                    .password("password")
                    .build();
            Teacher newTeacher = Teacher.builder()
                    .user(newUser)
                    .courses(List.of())
                    .profilePicture("path/to/profile/picture")
                    .build();
            users.add(newUser);
            teachers.add(newTeacher);
        }
        teacherRepository.saveAll(teachers);

        // Act
        Optional<Teacher> teacherFoundByUserId = teacherRepository.findByUserId(users.get(0).getId());

        // Assert
        Assertions.assertThat(teacherFoundByUserId).isPresent();
        Assertions.assertThat(teacherFoundByUserId.get()).isEqualTo(teachers.get(0));

    }

}
