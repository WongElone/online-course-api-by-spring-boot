package com.elonewong.onlinecourseapi.config;

import com.elonewong.onlinecourseapi.csr.user.Permission;
import com.elonewong.onlinecourseapi.csr.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // csrf is not necessary when using token-based authentication (e.g. jwt)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/categories", "/api/v1/categories/*",
                                "/api/v1/courses", "/api/v1/courses/*",
                                "/swagger-config", "/swagger-ui", "/swagger-ui/**", "/api-docs", "/api-docs/**")
                        .permitAll()
                        // comment out because authorization is implemented via enabling @EnableMethodSecurity and add @PreAuthorize at controllers' method level
//                        .requestMatchers("/api/v1/demo/teachers/**").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
//                        .requestMatchers(HttpMethod.GET, "/api/v1/demo/teachers/**").hasAnyAuthority(Permission.TEACHER_READ.name(), Permission.ADMIN_READ.name())
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(config -> config
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // jwtAuthfilter before UsernamePasswordAuthenticationFilter
        return http.build();
    }
}
