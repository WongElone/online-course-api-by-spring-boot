package com.elonewong.onlinecourseapi.config;

import com.elonewong.onlinecourseapi.csr.student.Student;
import com.elonewong.onlinecourseapi.csr.student.StudentRepository;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;
import com.elonewong.onlinecourseapi.csr.teacher.TeacherRepository;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.csr.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * for authenticating jwt token, not for creating jwt token if token not found, token not found should redirect to authentication endpoint to get jwt token
 */
@Component
@RequiredArgsConstructor // necessary
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_HEADER_PREFIX = "Bearer ";
    public static final String USER_PROFILE_KEY = "userProfile";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 1. check existence of jwt token in incoming request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_PREFIX)) { // if no jwt token
            filterChain.doFilter(request, response); // pass to next filter
            return;
        }
        jwt = authHeader.substring(AUTH_HEADER_PREFIX.length());
        userEmail = jwtService.extractUsername(jwt); // extract user email from jwt token;
        if (userEmail != null // user email is found in jwt
                                // note that if user email is not found, request will be rejected by next filter, which is UsernamePasswordAuthenticationFilter (see SecurityConfiguration.java)
                && SecurityContextHolder.getContext().getAuthentication() == null // no authentication in security context holder (a in memory authentication context)
        ) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request) // give the request context to the details of the auth token
                );
                SecurityContextHolder.getContext().setAuthentication(authToken); // this final step put the generated token to the security context holder
                
                User user = (User) userDetails;
                if (Role.TEACHER.equals(user.getRole())) {
                    Teacher teacher = teacherRepository.findByUserId(user.getId()).orElseThrow(RuntimeException::new);
                    request.setAttribute(USER_PROFILE_KEY, new Profile(user.getId(), teacher.getId(), user.getRole()));
                } else if (Role.STUDENT.equals(user.getRole())) {
                    Student student = studentRepository.findByUserId(user.getId()).orElseThrow(RuntimeException::new);
                    request.setAttribute(USER_PROFILE_KEY, new Profile(user.getId(), student.getId(), user.getRole()));
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
