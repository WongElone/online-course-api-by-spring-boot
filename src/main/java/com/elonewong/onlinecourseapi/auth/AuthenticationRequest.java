package com.elonewong.onlinecourseapi.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotBlank
    @Pattern(regexp = "^(.+)@(\\S+)$")
    private String email;

    @NotBlank
    @Size(min = 8, max = 55)
    private String password;

}
