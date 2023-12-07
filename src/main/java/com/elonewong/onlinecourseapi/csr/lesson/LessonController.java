package com.elonewong.onlinecourseapi.csr.lesson;

import com.elonewong.onlinecourseapi.advice.ApplicationExceptionHandler;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import com.elonewong.onlinecourseapi.exception.AuthorizationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/lessons")
public class LessonController {

    private final LessonService lessonService;

    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Operation(summary = "Get one lesson by lesson id and course id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson found"),
            @ApiResponse(responseCode = "404", description = "Lesson not found or course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @GetMapping("/{lessonId}")
    public LessonResponse getOneLesson(@PathVariable String lessonId,
                                       @PathVariable String courseId,
                                       HttpServletRequest httpRequest) throws AuthorizationException {
        return lessonService.getOneLesson(lessonId, courseId, Profile.extractUserProfile(httpRequest));
    }

    @Operation(summary = "Add one lesson to a course (For Teacher account only, which has to be teacher of the course)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully added the lesson"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public LessonResponse addOneLesson(@PathVariable String courseId,
                                       @Schema(implementation = LessonRequest.class, description = "Lesson to be added") @RequestBody @Valid LessonRequest lessonRequest,
                                       HttpServletRequest httpRequest) throws AuthorizationException {
        return lessonService.addOneLesson(courseId, lessonRequest, Profile.extractUserProfile(httpRequest));
    }

    @Operation(summary = "Update one lesson of a course (For Teacher account only, which has to be teacher of the course)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the lesson"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Lesson not found or course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PutMapping("/{lessonId}")
    @PreAuthorize("hasRole('TEACHER')")
    public LessonResponse updateOneLesson(@PathVariable String lessonId,
                                          @PathVariable String courseId,
                                          @Schema(implementation = LessonRequest.class, description = "Lesson to be updated") @RequestBody @Valid LessonRequest lessonRequest,
                                          HttpServletRequest httpRequest) throws AuthorizationException {
        return lessonService.updateOneLesson(lessonId, courseId, lessonRequest, Profile.extractUserProfile(httpRequest));
    }

    @Operation(summary = "Delete one lesson of a course (For Teacher account only, which has to be teacher of the course)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the lesson"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Lesson not found or course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{lessonId}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteOneLesson(@PathVariable String lessonId,
                                @PathVariable String courseId,
                                HttpServletRequest httpRequest) throws AuthorizationException {
        lessonService.deleteOneLesson(lessonId, courseId, Profile.extractUserProfile(httpRequest));
    }

}
