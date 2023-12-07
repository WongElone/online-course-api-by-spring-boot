package com.elonewong.onlinecourseapi.csr.upload;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
//import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Component
@Configuration
public class S3Config {

    @Bean
    public Region region() {
        return Region.AP_SOUTHEAST_1;
    }

//    @Bean
//    public ProfileCredentialsProvider profileCredentialsProvider() {
//        return ProfileCredentialsProvider.create();
//    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
//                .credentialsProvider(
//                    this.profileCredentialsProvider()
//                )
                .region(this.region())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
//                .credentialsProvider(
//                    this.profileCredentialsProvider()
//                )
                .region(this.region())
                .build();
    }

}
