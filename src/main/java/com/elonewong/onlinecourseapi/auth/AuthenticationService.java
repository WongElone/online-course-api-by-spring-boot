package com.elonewong.onlinecourseapi.auth;

import com.elonewong.onlinecourseapi.config.JwtAuthenticationFilter;
import com.elonewong.onlinecourseapi.config.JwtService;
import com.elonewong.onlinecourseapi.csr.student.Student;
import com.elonewong.onlinecourseapi.csr.student.StudentRepository;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;
import com.elonewong.onlinecourseapi.csr.teacher.TeacherRepository;
import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.csr.user.User;
import com.elonewong.onlinecourseapi.csr.user.UserRepository;
import com.elonewong.onlinecourseapi.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        repository.save(user);
        if (Role.TEACHER.equals(user.getRole())) {
            teacherRepository.save(Teacher.builder().user(user).courses(Collections.emptyList()).build());
        } else if (Role.STUDENT.equals(user.getRole())) {
            studentRepository.save(Student.builder().user(user).courses(Collections.emptyList()).build());
        } else throw new BadRequestException("invalid user role");

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(
            HttpServletRequest request
    ) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(JwtAuthenticationFilter.AUTH_HEADER_PREFIX)) {
            throw new RuntimeException("Refresh token not found.");
        }

        String refreshToken = authHeader.substring(JwtAuthenticationFilter.AUTH_HEADER_PREFIX.length());
        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = repository
                    .findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                return AuthenticationResponse.builder()
                        .accessToken(jwtService.generateToken(user))
                        .refreshToken(jwtService.generateRefreshToken(user))
                        .build();
            }
        }

        throw new RuntimeException("Refresh token invalid.");
    }
}
