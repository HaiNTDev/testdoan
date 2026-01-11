package com.example.Automated.Application.Mangament.serviceImplements;

import com.cloudinary.Cloudinary;
import com.example.Automated.Application.Mangament.serviceInterfaces.UploadServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.cloudinary.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;



@Service
@RequiredArgsConstructor
@Slf4j
public class UploadServiceImplement implements UploadServiceInterface {
    private final Cloudinary cloudinary;

    @Override
    public ResponseEntity<String> uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("File is null or has no filename.");
        }

        String publicValue = generatePublicValue(file.getOriginalFilename());
        String extension = getFileExtension(file.getOriginalFilename());
        File fileUpload = convert(file);
        log.info("Uploading file: {}", fileUpload.getAbsolutePath());

        String resourceType = determineResourceType(extension);

        Map<String, Object> uploadResult = cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap(
                "public_id", publicValue,
                "resource_type", resourceType
        ));

        cleanDisk(fileUpload);

        String uploadedUrl = uploadResult.get("secure_url").toString();
        log.info("Upload successful, file URL: {}", uploadedUrl);
        return ResponseEntity.status(HttpStatus.OK).body(uploadedUrl);
    }

    @Override
    public ResponseEntity<String> updateFile(MultipartFile file, String oldPublicId) throws IOException {
        if (file == null || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("File is null or has no filename.");
        }

        // Nếu có file cũ, xóa trước khi upload mới
        if (StringUtils.isNotBlank(oldPublicId)) {
            log.info("Old file public ID (before extraction): {}", oldPublicId);
            String extractedPublicId = extractPublicId(oldPublicId);
            log.info("Extracted old file public ID: {}", extractedPublicId);
            deleteFile(extractedPublicId); // Xóa file cũ trên Cloudinary
        }

        // Tạo Public ID mới
        String publicValue = generatePublicValue(file.getOriginalFilename());
        String extension = getFileExtension(file.getOriginalFilename());
        File fileUpload = convert(file);

        String resourceType = determineResourceType(extension);

        log.info("Uploading new file with Public ID: {}", publicValue);

        // Upload file mới vào Cloudinary
        Map<String, Object> uploadResult = cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap(
                "public_id", publicValue,
                "resource_type", resourceType,
                "overwrite", true // Ghi đè nếu tồn tại
        ));

        log.info("Upload result: {}", uploadResult);
        cleanDisk(fileUpload);

        String uploadedUrl = uploadResult.get("secure_url").toString();
        return ResponseEntity.status(HttpStatus.OK).body(uploadedUrl);
    }

    @Override
    public void deleteFile(String oldPublicId) throws IOException {
        if (StringUtils.isBlank(oldPublicId)) {
            log.warn("Empty oldPublicId received, skipping delete.");
            return;
        }
        try {
            String publicId = extractPublicId(oldPublicId);
            log.info("Trying to delete file: {}", publicId);
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                    "invalidate", true // Xóa cache
            ));
            log.info("Delete file result: {}", result);
            if ("not found".equals(result.get("result"))) {
                log.error("File not found on Cloudinary: {}", publicId);
            }
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            throw new IOException("Error deleting file: " + e.getMessage(), e);
        }
    }

    private String determineResourceType(String extension) {
        if ("pdf".equalsIgnoreCase(extension)) {
            return "raw"; // For PDF files
        } else {
            return "image"; // For images
        }
    }

    private String extractPublicId(String url) {
        if (StringUtils.isBlank(url)) return "";
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            int uploadIndex = path.indexOf("/upload/");
            if (uploadIndex == -1) {
                log.warn("Invalid Cloudinary URL: {}", url);
                return url;
            }
            String publicIdWithExt = path.substring(uploadIndex + 8); // Bỏ "/upload/"
            if (publicIdWithExt.startsWith("v")) {
                int firstSlash = publicIdWithExt.indexOf("/");
                if (firstSlash != -1) {
                    publicIdWithExt = publicIdWithExt.substring(firstSlash + 1);
                }
            }
            int dotIndex = publicIdWithExt.lastIndexOf(".");
            String publicId = (dotIndex != -1) ? publicIdWithExt.substring(0, dotIndex) : publicIdWithExt;
            log.info("Extracted public ID: {}", publicId);
            return publicId;
        } catch (Exception e) {
            log.error("Failed to extract public ID: {}", e.getMessage(), e);
            return url;
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf('.');
        return (lastDot == -1) ? "" : filename.substring(lastDot + 1).toLowerCase();
    }

    private void cleanDisk(File file) {
        try {
            Path filePath = file.toPath();
            Files.delete(filePath);
        } catch (Exception e) {
            log.error("Error cleaning disk: {}", e.getMessage());
        }
    }

    private File convert(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("File has no original filename.");
        }
        String fileName = originalFilename.toLowerCase();
        if (fileName.endsWith(".heic")) {
            throw new IOException("HEIC file not supported! Please convert to PNG or JPG before uploading.");
        }
        String[] fileParts = getFileName(originalFilename);
        File convFile = new File(UUID.randomUUID().toString() + "." + fileParts[1]);
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, convFile.toPath());
        }
        return convFile;
    }

    private String generatePublicValue(String originalName) {
        String fileName = getFileName(originalName)[0];
        return UUID.randomUUID().toString() + "_" + fileName;
    }

    private String[] getFileName(String originalName) {
        return originalName.split("\\.");
    }
}
