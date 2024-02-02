package com.ssafy.server.common.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class S3FileUploader {

    @Autowired
    private AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;

    public String uploadFile(MultipartFile file) throws IOException {
        try {
//            String fileName=file.getOriginalFilename();
            String fileName = String.valueOf(UUID.randomUUID());    // 파일명이 한글이면 Url 깨져서 일단 uuid로 대체
            String fileUrl= "https://" + bucket + ".s3."+ region + ".amazonaws.com/" +fileName;
            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket,fileName,file.getInputStream(),metadata);
            return fileUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
