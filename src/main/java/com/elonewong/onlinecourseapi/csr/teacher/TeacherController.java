package com.elonewong.onlinecourseapi.csr.teacher;

import com.elonewong.onlinecourseapi.advice.ApplicationExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    private final TeacherService service;

    @Autowired
    public TeacherController(TeacherService service) {
        this.service = service;
    }

    @Operation(summary = "Get existing teachers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teachers found")
    })
    @GetMapping
    public List<TeacherResponse> getAllTeachers() {
        return service.getAllTeachers();
    }

    @Operation(summary = "Get one teacher by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher found"),
            @ApiResponse(responseCode = "404", description = "Teacher not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public TeacherResponse getOneTeacher(@PathVariable String id) {
        return service.getOneTeacher(id);
    }

}
