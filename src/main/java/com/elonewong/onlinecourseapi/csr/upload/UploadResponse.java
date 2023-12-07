package com.elonewong.onlinecourseapi.csr.upload;

public record UploadResponse(
    String fileName,
    String fileExtension,
    String urlString
) {}
