package com.ssafy.server.karaoke.controller;

import com.amazonaws.util.IOUtils;
import com.ssafy.server.common.util.S3FileUploader;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/karaoke/file")
public class RecordingFileController {

    @Autowired
    private S3FileUploader fileUploader;

    private static final String USERNAME = "OPENVIDUAPP";
    private static final String PASSWORD = "haebang";

    @PostMapping("/upload")
    public ResponseEntity<String> downloadAndUploadFile(@RequestBody Map<String, Object> params) throws IOException {

        // 파일 다운로드
        String fileUrl = (String) params.get("fileUrl");
        String destinationPath = String.valueOf(UUID.randomUUID()) + ".mp4";

        // 파일 다운로드
        downloadFile(fileUrl, destinationPath);

        File file = new File(new File("").getAbsolutePath() + "/" + destinationPath);
        FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

        try {
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            input.close();
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패");
        }

        //jpa.png -> multipart 변환
        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

        String s3FileUrl = fileUploader.uploadFile(multipartFile);

        // 파일 삭제
        Files.delete(file.toPath());

        if (s3FileUrl.isEmpty()) {    // 파일 업로드 실패한 경우
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패");
        } else {    // 파일 업로드 성공. fileUrl로 접속 시 해당 파일 열람 가능.
            return ResponseEntity.ok(s3FileUrl);
        }
    }

    public static void downloadFile(String urlString, String destinationPath) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Basic Authentication 설정
        String auth = USERNAME + ":" + PASSWORD;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;
        connection.setRequestProperty("Authorization", authHeader);

        try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream()); FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } finally {
            connection.disconnect();
        }
    }

}