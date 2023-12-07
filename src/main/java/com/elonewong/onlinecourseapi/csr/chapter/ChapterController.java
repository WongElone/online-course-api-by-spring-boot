package com.elonewong.onlinecourseapi.csr.chapter;

import com.elonewong.onlinecourseapi.advice.ApplicationExceptionHandler;
import com.elonewong.onlinecourseapi.csr.lesson.LessonOrder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    @Autowired
    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @Operation(summary = "Add one chapter to a course (For Teacher account only, which has to be teacher of the course)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully added new chapter"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ChapterResponse addOneChapterForOneCourse(@PathVariable String courseId,
                                                     @Schema(implementation = ChapterRequest.class, description = "Chapter to be added") @RequestBody @Valid ChapterRequest chapterRequest,
                                                     HttpServletRequest httpRequest) throws AuthorizationException {
        return chapterService.addOneChapterForOneCourse(courseId, chapterRequest, Profile.extractUserProfile(httpRequest));
    }

    @Operation(summary = "Get existing chapters of a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of chapters found"),
    })
    @GetMapping
    public List<ChapterResponse> getAllChaptersOfOneCourse(@PathVariable String courseId, HttpServletRequest httpRequest) throws AuthorizationException {
        return chapterService.getAllChaptersOfOneCourse(courseId, Profile.extractUserProfile(httpRequest));
    }

    @Operation(summary = "Update one chapter of a course (For Teacher account only, which has to be teacher of the course)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully update the chapter"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Either chapter not found or course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PutMapping("/{chapterId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ChapterResponse updateOneChapterOfOneCourse(@PathVariable String courseId,
                                                       @PathVariable String chapterId,
                                                       @Schema(implementation = ChapterRequest.class, description = "Chapter to be updated") @RequestBody @Valid ChapterRequest chapterRequest,
                                                       HttpServletRequest httpRequest) throws AuthorizationException {
        return chapterService.updateOneChapterOfOneCourse(courseId, chapterId, chapterRequest, Profile.extractUserProfile(httpRequest));
    }

    @Operation(summary = "Update the orders of lessons of one chapter of a course (For Teacher account only, which has to be teacher of the course)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully update the orders of lessons"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Either chapter not found or course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PutMapping("/{chapterId}/lessonOrders")
    @PreAuthorize("hasRole('TEACHER')")
    public void updateLessonsOrderInOneChapter(@PathVariable String courseId,
                                               @PathVariable String chapterId,
                                               @ArraySchema(schema = @Schema(implementation = LessonOrder.class, description = "Order number describes the order of the lesson in the chapter, i.e. first lesson has an order number of 1")) @RequestBody List<@Valid LessonOrder> newLessonOrders,
                                               HttpServletRequest httpRequest) throws AuthorizationException {
        chapterService.updateLessonsOrder(courseId, chapterId, newLessonOrders, Profile.extractUserProfile(httpRequest));
    }

    @Operation(summary = "Delete one chapter of a course (For Teacher account only, which has to be teacher of the course)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully delete the chapter"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Either chapter not found or course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{chapterId}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteOneChapter(@PathVariable String courseId,
                                 @PathVariable String chapterId,
                                 HttpServletRequest httpRequest) throws AuthorizationException {
        chapterService.deleteOneChapter(courseId, chapterId, Profile.extractUserProfile(httpRequest));
    }

}
