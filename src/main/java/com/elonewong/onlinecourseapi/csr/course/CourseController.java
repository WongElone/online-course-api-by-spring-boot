package com.elonewong.onlinecourseapi.csr.course;

import com.elonewong.onlinecourseapi.advice.ApplicationExceptionHandler;
import com.elonewong.onlinecourseapi.csr.chapter.ChapterOrder;
import com.elonewong.onlinecourseapi.csr.student.StudentResponse;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import com.elonewong.onlinecourseapi.exception.AuthorizationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Get existing course categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of courses found"),
    })
    @GetMapping
    public List<CourseResponse> getAllCourses(HttpServletRequest httpRequest) {
        return courseService.getAllCourses();
    }

    @Operation(summary = "Get a course by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the course"),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public CourseResponse getOneCourse(@PathVariable String id) {
        return courseService.getOneCourse(id);
    }

    @Operation(summary = "Get existing students of the course by the course's id (For Teacher account only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of students found in the course"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @GetMapping("/{courseId}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public List<StudentResponse> getStudentsOfTheCourse(@PathVariable String courseId, HttpServletRequest httpRequest) throws AuthorizationException {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        courseService.validateIsTeacherOfTheCourse(userProfile, courseId);
        return courseService.getStudentsOfTheCourse(courseId);
    }

    @Operation(summary = "Add a new course (For Teacher account only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New course added successfully"),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('TEACHER')")
    public CourseResponse addOneCourse(@Schema(description = "Course to add", implementation = CourseRequest.class) @RequestBody @Valid CourseRequest courseRequest,
                                       HttpServletRequest httpRequest) {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        return courseService.addOneCourse(courseRequest, userProfile.profileTableId());
    }

    @Operation(summary = "Update an existing course (For Teacher account only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    public CourseResponse updateOneCourse(@PathVariable String courseId,
                                          @Schema(description = "Course to update", implementation = CourseRequest.class)
                                            @RequestBody @Valid CourseRequest courseRequest,
                                          HttpServletRequest httpRequest) throws AuthorizationException {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        courseService.validateIsTeacherOfTheCourse(userProfile, courseId);
        return courseService.updateOneCourse(courseId, courseRequest);
    }

    @Operation(summary = "Update the orders of chapters of an existing course (For Teacher account only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PutMapping("/{courseId}/chapterOrders")
    @PreAuthorize("hasRole('TEACHER')")
    public void updateChapterOrdersInOneCourse(@PathVariable String courseId,
                                               @ArraySchema(schema = @Schema(implementation = ChapterOrder.class, description = "Order number describes the order of the lesson in the chapter, i.e. first lesson has an order number of 1"))
                                                @RequestBody List<@Valid ChapterOrder> chapterOrders,
                                               HttpServletRequest httpRequest) throws AuthorizationException {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        courseService.updateChapterOrders(courseId, chapterOrders, userProfile);
    }

//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteOneCourse(@PathVariable String id) {
//        courseService.deleteOneCourse(id);
//    }

}
