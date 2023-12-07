package com.elonewong.onlinecourseapi.csr.upload;

import com.elonewong.onlinecourseapi.advice.ApplicationExceptionHandler;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import com.elonewong.onlinecourseapi.exception.AuthorizationException;
import com.elonewong.onlinecourseapi.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/uploads")
public class UploadController {

    private UploadService uploadService;

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    /**
     *
     * @param file
     * @param bucketEnumString
     * @param targetFolder
     * @return Upload Id
     */
    @Operation(description = "Upload a file to the AWS S3 cloud storage, if it succeeds, it will return ID of the upload. Two different buckets are available, one is public-read, another one is private-read.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public String uploadOneObject(@Parameter(description = "file to be uploaded") @RequestPart MultipartFile file,
                                @Parameter(description = "value either be 'PublicMedia' or 'PrivateMedia'") @RequestPart String bucketEnumString,
                                @Parameter(description = "give a valid name of folder to which the file want to be uploaded, if the folder doesn't exist, it will create one with the name provided. A valid folder name should START WITHOUT forward slashes, END WITH a single forward slash, it supports multiple levels of folders.") @RequestPart String targetFolder) {
        try {
            Upload.S3BucketEnum bucketEnum = Upload.S3BucketEnum.valueOf(bucketEnumString);
            return uploadService.uploadOneObject(file, bucketEnum, targetFolder);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid bucket enum string");
        }
    }

    @Operation(description = "Get an upload by id. If the upload is found, it will generate a pre-signed url for time-limited access to the file. The time limit is a duration of 5 minutes. The pre-signed url is given in the 'urlString' field in the response body.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upload found"),
            @ApiResponse(responseCode = "403", description = "You no right to access the upload, probably because the material is not shared to you.",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Upload not found",
                    content = @Content(schema = @Schema(implementation = ApplicationExceptionHandler.CommonErrorResponse.class)))
    })
    @GetMapping("/{uploadId}")
    public UploadResponse getOneUpload(@PathVariable String uploadId, HttpServletRequest httpRequest) throws AuthorizationException {
        return uploadService.getOneUpload(uploadId, Profile.extractUserProfile(httpRequest));
    }

}
