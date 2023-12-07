package com.elonewong.onlinecourseapi.csr.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {
//    USER(
//            Collections.emptySet()
//    ),
//    ADMIN(
//            Set.of(
//                    Permission.ADMIN_READ,
//                    Permission.ADMIN_CREATE,
//                    Permission.ADMIN_UPDATE,
//                    Permission.ADMIN_DELETE,
//                    Permission.STUDENT_READ,
//                    Permission.STUDENT_CREATE,
//                    Permission.STUDENT_UPDATE,
//                    Permission.STUDENT_DELETE,
//                    Permission.TEACHER_READ,
//                    Permission.TEACHER_CREATE,
//                    Permission.TEACHER_UPDATE,
//                    Permission.TEACHER_DELETE
//            )
//    ),
    STUDENT(
            Set.of(
            Permission.STUDENT_READ,
            Permission.STUDENT_CREATE,
            Permission.STUDENT_UPDATE,
            Permission.STUDENT_DELETE
            )
    ),
    TEACHER(
            Set.of(
            Permission.TEACHER_READ,
            Permission.TEACHER_CREATE,
            Permission.TEACHER_UPDATE,
            Permission.TEACHER_DELETE
            )
    )
    ;

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
