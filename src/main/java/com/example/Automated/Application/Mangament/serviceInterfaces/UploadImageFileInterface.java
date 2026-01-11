package com.example.Automated.Application.Mangament.serviceInterfaces;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Component
public interface UploadImageFileInterface {
    String uploadImage(MultipartFile file) throws IOException;

    String updateImage(MultipartFile file, String oldPublicId) throws IOException;

    void deleteImage(String oldPublicId) throws  IOException;
}
