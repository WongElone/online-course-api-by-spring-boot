package com.elonewong.onlinecourseapi.csr.student;

import com.elonewong.onlinecourseapi.advice.ApplicationExceptionHandler;
import com.elonewong.onlinecourseapi.csr.enrollment.*;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    @Autowired
    public StudentController(StudentService studentService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "Get one student by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student found"),
            @ApiResponse(responseCode = "404", description = "Student not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public StudentResponse getOneStudent(@PathVariable String id) {
        return studentService.getOneStudent(id);
    }

    @Operation(summary = "Get student information of current user if it is a student account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found student information"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Student",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentResponse getMyself(HttpServletRequest httpRequest) {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        return studentService.getOneStudent(userProfile.profileTableId());
    }

    @Operation(summary = "Get course enrollments of current user if it is a student account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found enrolled courses"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Student",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @GetMapping("/me/enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    public List<EnrollmentRespond> getMyEnrollments(HttpServletRequest httpRequest) {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        return enrollmentService.getEnrollmentsByStudentId(userProfile.profileTableId());
    }

    // enroll course
    @Operation(summary = "Enroll the current user to a course if it is a student account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully enrolled"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Student",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PostMapping("/me/enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentRespond enrollOneCourse(@Schema(implementation = EnrollCourseRequest.class, description = "Information for enrolling a course") @RequestBody @Valid EnrollCourseRequest enrollCourseRequest, HttpServletRequest httpRequest) {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        return enrollmentService.enrollOneCourse(enrollCourseRequest, userProfile.profileTableId());
    }

    //drop course
    @Operation(summary = "Drop the current user from a course if it is a student account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully dropped"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Student",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PostMapping("/me/enrollments/drop")
    @PreAuthorize("hasRole('STUDENT')")
    public EnrollmentRespond dropOneCourse(@Schema(implementation = DropCourseRequest.class, description = "Information for dropping a course") @RequestBody @Valid DropCourseRequest dropCourseRequest, HttpServletRequest httpRequest) {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        return enrollmentService.dropOneCourse(dropCourseRequest.courseId(), userProfile.profileTableId());
    }

}
