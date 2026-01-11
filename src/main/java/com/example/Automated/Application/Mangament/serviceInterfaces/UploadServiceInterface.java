package com.example.Automated.Application.Mangament.serviceInterfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadServiceInterface {
    ResponseEntity<String> uploadFile(MultipartFile file) throws IOException;

    ResponseEntity<String> updateFile(MultipartFile file, String oldPublicId) throws IOException;

    void deleteFile(String publicId) throws IOException;
}
