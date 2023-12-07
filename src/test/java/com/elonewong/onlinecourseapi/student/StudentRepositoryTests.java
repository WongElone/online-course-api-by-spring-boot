package com.elonewong.onlinecourseapi.student;

import com.elonewong.onlinecourseapi.csr.student.Student;
import com.elonewong.onlinecourseapi.csr.student.StudentRepository;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;
import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.csr.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class StudentRepositoryTests {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void test_StudentRepository_FindByUserId_ReturnCorrectStudent() {

        // Arrange
        List<User> users = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 2; i ++){
            User newUser = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("johndoe0" + i + "@example.com")
                    .role(Role.STUDENT)
                    .password("password")
                    .build();
            Student newStudent = Student.builder()
                    .user(newUser)
                    .courses(List.of())
                    .profilePicture("path/to/profile/picture")
                    .build();
            users.add(newUser);
            students.add(newStudent);
        }
        studentRepository.saveAll(students);

        // Act
        Optional<Student> studentFoundByUserId = studentRepository.findByUserId(users.get(0).getId());

        // Assert
        Assertions.assertThat(studentFoundByUserId).isPresent();
        Assertions.assertThat(studentFoundByUserId.get()).isEqualTo(students.get(0));

    }

}

