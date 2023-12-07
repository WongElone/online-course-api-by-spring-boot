package com.elonewong.onlinecourseapi.csr.upload;

import org.springframework.stereotype.Service;

@Service
public class UploadResponseGenerator {

    public UploadResponse generate(Upload upload, String urlString) {
        return new UploadResponse(
            upload.getFileName(),
            upload.getFileExtension(),
            urlString
        );
    }

}
