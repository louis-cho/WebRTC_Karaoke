package com.ssafy.server.feed.controller;

import com.ssafy.server.common.util.S3FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class FileUploadController {

    @Autowired
    private S3FileUploader fileUploader;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = fileUploader.uploadFile(file);
        if(fileUrl.equals("")) {    // 파일 업로드 실패한 경우
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } else {    // 파일 업로드 성공. fileUrl로 접속 시 해당 파일 열람 가능.
            return ResponseEntity.ok(fileUrl);
        }
    }
}
