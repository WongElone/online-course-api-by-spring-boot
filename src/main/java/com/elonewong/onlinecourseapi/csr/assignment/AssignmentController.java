package com.elonewong.onlinecourseapi.csr.assignment;

import com.elonewong.onlinecourseapi.advice.ApplicationExceptionHandler;
import com.elonewong.onlinecourseapi.csr.lesson.LessonRequest;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import com.elonewong.onlinecourseapi.exception.AuthorizationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@Validated
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @Operation(summary = "Add an assignment to a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Assignment successfully added to the course"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public AssignmentResponse addOneAssignmentToCourse(@Schema(implementation = LessonRequest.class, description = "Assignment to be added") @RequestBody @Valid AssignmentRequest assignmentRequest,
                                                       @RequestParam @NotNull String courseId,
                                                       HttpServletRequest httpRequest) throws AuthorizationException {
        return assignmentService.addOneAssignmentToCourse(assignmentRequest, courseId, Profile.extractUserProfile(httpRequest));
    }

    @Operation(summary = "Get assignments of one course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignments found in the course"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Student or Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @GetMapping
    public List<AssignmentResponse> getAssignmentsOfOneCourse(@RequestParam @NotNull String courseId,
                                                              HttpServletRequest httpRequest) throws AuthorizationException {
        return assignmentService.getAssignmentsOfOneCourse(courseId, Profile.extractUserProfile(httpRequest));
    }

    @Operation(summary = "Update an assignment to a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Assignment successfully updated"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Assignment not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PutMapping("/{assignmentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public AssignmentResponse updateOneAssignmentOfCourse(@PathVariable String assignmentId,
                                                          @Schema(implementation = LessonRequest.class, description = "Assignment to be updated") @RequestBody @Valid AssignmentRequest assignmentRequest,
                                                          HttpServletRequest httpRequest) throws AuthorizationException {
        return assignmentService.updateOneAssignment(assignmentId, assignmentRequest, Profile.extractUserProfile(httpRequest));
    }

    @Operation(summary = "Update an assignment to a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Assignment successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Assignment not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOneAssignmentOfCourse(@PathVariable String assignmentId,
                                            HttpServletRequest httpRequest) throws AuthorizationException {
        assignmentService.deleteOneAssignment(assignmentId, Profile.extractUserProfile(httpRequest));
    }

}
