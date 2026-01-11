package com.example.Automated.Application.Mangament.serviceImplements;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Automated.Application.Mangament.serviceInterfaces.UploadImageFileInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class UploadImageFileService  implements UploadImageFileInterface {
    private final Cloudinary cloudinary;


    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("File is null or has no filename.");
        }

        String publicValue = generatePublicValue(file.getOriginalFilename());
        String extension = getFileExtension(file.getOriginalFilename());
        File fileUpload = convert(file);

        log.info("Uploading file: {}", fileUpload.getAbsolutePath());

        Map<String, Object> uploadResult = cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap(
                "public_id", publicValue
        ));

        cleanDisk(fileUpload);

        String uploadedUrl = cloudinary.url().generate(publicValue + "." + extension);
        log.info("Upload successful, file URL: {}", uploadedUrl);

        return uploadedUrl;
    }

    public String updateImage(MultipartFile file, String oldPublicId) throws IOException {
        if (file == null || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("File is null or has no filename.");
        }

        // Nếu có ảnh cũ, xóa trước khi upload ảnh mới
        if (StringUtils.isNotBlank(oldPublicId)) {
            log.info("Old image public ID (before extraction): {}", oldPublicId);

            // Trích xuất public_id từ URL nếu cần
            String extractedPublicId = extractPublicId(oldPublicId);
            log.info("Extracted old image public ID: {}", extractedPublicId);

            deleteImage(extractedPublicId); // Xóa ảnh cũ trên Cloudinary
        }

        // Tạo Public ID mới
        String publicValue = generatePublicValue(file.getOriginalFilename());
        String extension = getFileExtension(file.getOriginalFilename());
        File fileUpload = convert(file);

        String fullPublicId = "pet_images/" + publicValue;
        log.info("Uploading new file with Public ID: {}", fullPublicId);

        // Upload ảnh mới vào Cloudinary
        Map<String, Object> uploadResult = cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap(
                "public_id", fullPublicId,
                "resource_type", "image"
        ));

        log.info("Upload result: {}", uploadResult);

        cleanDisk(fileUpload);

        return uploadResult.get("secure_url").toString(); // Trả về URL ảnh
    }

    public void deleteImage(String oldPublicId) {
        if (StringUtils.isBlank(oldPublicId)) {
            log.warn("Empty oldPublicId received, skipping delete.");
            return;
        }

        try {
            String publicId = extractPublicId(oldPublicId);

            log.info("Trying to delete image: {}", publicId);

            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                    "invalidate", true // Xóa cache
            ));

            log.info("Delete image result: {}", result);

            if ("not found".equals(result.get("result"))) {
                log.error("Image not found on Cloudinary: {}", publicId);
            }
        } catch (Exception e) {
            log.error("Error deleting image: {}", e.getMessage(), e);
        }
    }

    public String extractPublicId(String url) {
        if (StringUtils.isBlank(url)) return "";



        try {
            // Bỏ phần query params nếu có
            URI uri = new URI(url);
            String path = uri.getPath();

            // Lấy phần sau "/upload/"
            int uploadIndex = path.indexOf("/upload/");
            if (uploadIndex == -1) {
                log.warn("Invalid Cloudinary URL: {}", url);
                return url;
            }

            // Lấy public_id từ đường dẫn sau "/upload/"
            String publicIdWithExt = path.substring(uploadIndex + 8); // Bỏ "/upload/"

            if (publicIdWithExt.startsWith("v")) {
                int firstSlash = publicIdWithExt.indexOf("/");
                if (firstSlash != -1) {
                    publicIdWithExt = publicIdWithExt.substring(firstSlash + 1);
                }
            }

            // Xóa phần mở rộng (.png, .jpg,...)
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
        return (lastDot == -1) ? "" : filename.substring(lastDot + 1);
    }

    private void cleanDisk(File file){
        try{
            Path filePath = file.toPath();
            Files.delete(filePath);
        }catch (Exception e){
            log.error("Error");
        }
    }

    private File convert(MultipartFile file) throws IOException{
        assert file.getOriginalFilename() != null;
        String fileName = file.getOriginalFilename().toLowerCase();
        if (fileName.endsWith(".heic")) {
            throw new IOException("❌ File HEIC không được hỗ trợ! Vui lòng chuyển đổi sang PNG hoặc JPG trước khi tải lên.");
        }
        File convFile = new File(StringUtils.join(generatePublicValue(file.getOriginalFilename()),getFileName(file.getOriginalFilename())[1]));
        try(InputStream is = file.getInputStream()){
            Files.copy(is, convFile.toPath());
        }
        return convFile;
    }

    private String generatePublicValue(String originalName){
        String fileName = getFileName(originalName)[0];
        return StringUtils.join(UUID.randomUUID().toString(), "_", fileName);
    }



    private String[] getFileName(String originalName){
        return originalName.split("\\.");
    }
}
