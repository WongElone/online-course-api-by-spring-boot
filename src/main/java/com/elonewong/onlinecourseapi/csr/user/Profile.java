package com.elonewong.onlinecourseapi.csr.user;

import com.elonewong.onlinecourseapi.config.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;

public record Profile(
    String userId,
    String profileTableId, // i.e. teacher or student id
    Role role
) {
    public static Profile extractUserProfile(HttpServletRequest httpServletRequest) {
        return (Profile) httpServletRequest.getAttribute(JwtAuthenticationFilter.USER_PROFILE_KEY);
    }
}