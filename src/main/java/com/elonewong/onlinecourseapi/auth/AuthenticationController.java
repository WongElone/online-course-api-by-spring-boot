package com.elonewong.onlinecourseapi.auth;

import com.elonewong.onlinecourseapi.advice.ApplicationExceptionHandler;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Authentication Endpoints")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    @Autowired
    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }
    @Operation(description = "Provide necessary info to register a new account for accessing this Online Course API. \nThere are only two types of account, 'Teacher' and 'Student'. In a nutshell, teachers provide online course materials, while students enroll in the courses. \nSuccessful account registration will give an access key and refresh key in the response body, they are in the format of Jason Web Token. Access key must be put in the 'Authorization' header as a Bearer token (i.e. 'Bearer <access_key_value>') while requesting the authentication-protected endpoints of this API, otherwise the access will be denied. \nAccess key expires in 1 day, refresh key expires in 7 days. \nIf you are testing the API with swagger-ui in browser, you may consider using a browser plugin called 'ModHeader' to add this header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(description = "Get access key and refresh key by providing account credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered")
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(description = "When access key expired, provide the Bearer token with the refresh key in the 'Authorization' header, and then request this endpoint. This will return regenerated access key and refresh key.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(service.refreshToken(httpRequest));
    }
}
