package com.elonewong.onlinecourseapi.user;

import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.csr.user.User;
import com.elonewong.onlinecourseapi.csr.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test_UserRepository_FindUserByEmail_ReturnCorrectUser() {

        // Arrange
        User newUser = User.builder()
                .email("johndoe@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.STUDENT)
                .password("$2a$10$q.A37vTrXLxUgfZ7Tte.KuVCMgv0PlfZN11GPPaLlPQPExpgVP8Hy")
                .build();
        userRepository.save(newUser);

        // Act
        Optional<User> userFoundByEmail = userRepository.findByEmail(newUser.getEmail());

        // Assert
        Assertions.assertThat(userFoundByEmail).isPresent();
        userFoundByEmail.ifPresent(user -> Assertions.assertThat(newUser).isEqualTo(userFoundByEmail.get()));

    }
}
