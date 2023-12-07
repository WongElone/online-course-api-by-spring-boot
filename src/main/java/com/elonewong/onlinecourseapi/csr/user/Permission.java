package com.elonewong.onlinecourseapi.csr.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

//    ADMIN_READ("admin:read"),
//    ADMIN_CREATE("admin:create"),
//    ADMIN_UPDATE("admin:update"),
//    ADMIN_DELETE("admin:delete"),

    STUDENT_READ("student:read"),
    STUDENT_CREATE("student:create"),
    STUDENT_UPDATE("student:update"),
    STUDENT_DELETE("student:delete"),

    TEACHER_READ("teacher:read"),
    TEACHER_CREATE("teacher:create"),
    TEACHER_UPDATE("teacher:update"),
    TEACHER_DELETE("teacher:delete"),
    ;

    @Getter
    private final String permission;
}
