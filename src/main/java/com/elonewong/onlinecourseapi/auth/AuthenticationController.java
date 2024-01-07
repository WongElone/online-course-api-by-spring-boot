package com.elonewong.onlinecourseapi.auth;

import com.elonewong.onlinecourseapi.advice.ApplicationExceptionHandler;
import com.elonewong.onlinecourseapi.csr.user.User;
import com.elonewong.onlinecourseapi.exception.RegisterUserBadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "1. Authentication Endpoints")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    @Autowired
    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }
    @Operation(summary = "User login", description = "Provide necessary info to register a new account for accessing this Online Course API. \nThere are only two types of account, 'Teacher' and 'Student'. In a nutshell, teachers provide online course materials, while students enroll in the courses. \nSuccessful account registration will give an access token and refresh token in the response body, they are in the format of Jason Web Token. Access token must be put in the 'Authorization' header as a Bearer token (i.e. 'Bearer <access_token_value>') while requesting the authentication-protected endpoints of this API, otherwise the access will be denied. \nAccess token expires in 1 day, refresh token expires in 7 days. \nIf you are testing the API with swagger-ui in browser, you may consider using a browser plugin called 'ModHeader' to add this header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered"),
            @ApiResponse(responseCode = "400", description = "Either invalid email, or invalid password, or email already used by others",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationResponse register(
            @RequestBody @Valid RegisterRequest request
    ) throws RegisterUserBadRequestException {
        return service.register(request);
    }

    @Operation(summary = "Get access token", description = "Get access token and refresh token by providing account credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "400", description = "Email and password doesn't match",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return service.authenticate(request);
    }

    @Operation(summary = "Refresh tokens", description = "When access token expired, provide the Bearer token with the refresh token in the 'Authorization' header, and then request this endpoint. This will return regenerated access token and refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens refreshed"),
            @ApiResponse(responseCode = "400", description = "Refresh token not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Refresh token expired",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Refresh token invalid",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse refresh(HttpServletRequest httpRequest) {
        return service.refreshToken(httpRequest);
    }
}
