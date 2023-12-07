package com.elonewong.onlinecourseapi.csr.partnershipappeal;

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
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/partnership_appeals")
@PreAuthorize("hasRole('TEACHER')")
@Validated
public class PartnershipAppealController {

    private final PartnershipAppealService partnershipAppealService;

    @Autowired
    public PartnershipAppealController(PartnershipAppealService partnershipAppealService) {
        this.partnershipAppealService = partnershipAppealService;
    }

    @Operation(summary = "Make an appeal to join a course as a teacher for collaborative work")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully made an appeal"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('TEACHER')")
    public PartnershipAppealResponse addOnePartnershipAppeal(@Schema(implementation = LessonRequest.class, description = "Details of the appeal") @RequestBody @Valid PartnershipAppealRequest partnershipAppealRequest,
                                                             HttpServletRequest httpRequest) {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        return partnershipAppealService.addOnePartnershipAppeal(partnershipAppealRequest, userProfile);
    }

    @Operation(summary = "Get all appeals made by other teachers who want to join the course for collaborative work")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found existing partnership appeals of the course"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public List<PartnershipAppealResponse> getAllPartnershipAppealsOfOneCourse(@RequestParam @NotBlank String courseId,
                                                                               HttpServletRequest httpRequest) throws AuthorizationException {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        return partnershipAppealService.getAllPartnershipAppealsOfOneCourse(courseId, userProfile);
    }

    @Operation(summary = "Get my partnership appeals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found existing partnership appeals made by me"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @GetMapping("/mine")
    @PreAuthorize("hasRole('TEACHER')")
    public List<PartnershipAppealResponse> getAllPartnershipAppealsOfMine(HttpServletRequest httpRequest) throws AuthorizationException {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        return partnershipAppealService.getAllPartnershipAppealsOfOneTeacher(userProfile);
    }

    @Operation(summary = "Approve an appeal made by another teacher who want to join the course for collaborative work")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully approved the appeal"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Appeal not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @PutMapping
    @PreAuthorize("hasRole('TEACHER')")
    public void approveOnePartnershipAppeal(@Schema(implementation = LessonRequest.class, description = "The appeal to be approved") @RequestBody @Valid PartnershipAppealCompositeIdRequest appealCompositeIdRequest,
                                            HttpServletRequest httpRequest) throws AuthorizationException {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        partnershipAppealService.approveOnePartnershipAppeal(appealCompositeIdRequest, userProfile);
    }

    @Operation(summary = "Reject an partnership appeal by deleting it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted an appeal"),
            @ApiResponse(responseCode = "403", description = "Only allow access by Teacher of the course",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Appeal not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @DeleteMapping
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOnePartnershipAppeal(@Schema(implementation = LessonRequest.class, description = "The appeal to be deleted") @RequestBody @Valid PartnershipAppealCompositeIdRequest appealCompositeIdRequest,
                                           HttpServletRequest httpRequest) throws AuthorizationException {
        Profile userProfile = Profile.extractUserProfile(httpRequest);
        partnershipAppealService.deleteOnePartnershipAppeal(appealCompositeIdRequest, userProfile);
    }

}
