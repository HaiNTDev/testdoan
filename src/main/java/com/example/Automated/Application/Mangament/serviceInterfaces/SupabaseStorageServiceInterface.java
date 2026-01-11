package com.example.Automated.Application.Mangament.serviceInterfaces;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface SupabaseStorageServiceInterface {
    public String uploadNewFile(MultipartFile file, String folderPath);

    public void deleteFile(String oldPublicUrl);

    public String updateFile(MultipartFile newFile, String oldPublicUrl, String folderPath);

    public String getMimeTypeFromUrl(String fileUrl);
}
