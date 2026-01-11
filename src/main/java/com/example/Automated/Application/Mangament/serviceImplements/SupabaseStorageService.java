package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.serviceInterfaces.SupabaseStorageServiceInterface;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


@Service
public class SupabaseStorageService implements SupabaseStorageServiceInterface {
    private static final String STORAGE_API_PATH = "/storage/v1/object/";

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",

            "text/plain",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword", // .doc
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-excel" // .xls

    );


    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    @Value("${supabase.storage.bucket-name}")
    private String bucketName;


    private WebClient webClient;


    public SupabaseStorageService(WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.build();
    }


    @PostConstruct
    public void init() {

    }


    public String uploadNewFile(MultipartFile file, String folderPath) {

        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = System.currentTimeMillis() + "_" + originalFilename;
        String pathInStorage = folderPath + "/" + uniqueFileName;

        String contentType = file.getContentType();



        System.out.println("Kiểm tra Tệp: " + originalFilename + ", MIME Type: " + contentType);


        if (contentType != null && contentType.equalsIgnoreCase("application/pdf")) {
            if (!originalFilename.toLowerCase().endsWith(".pdf")) {

                throw new RuntimeException("Tệp PDF phải có đuôi mở rộng là .pdf.");
            }

        }

        else if (contentType == null || (!contentType.startsWith("image/") && !contentType.equalsIgnoreCase("application/pdf") /* ... */)) {
            throw new RuntimeException("Định dạng tệp không được hỗ trợ.");
        }

        String fullUploadUrl = supabaseUrl + STORAGE_API_PATH + bucketName + "/" + pathInStorage;

        try {
            webClient.put()
                    .uri(fullUploadUrl)
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("Content-Type", file.getContentType())
                    .body(BodyInserters.fromValue(file.getBytes()))
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse ->
                            Mono.error(new RuntimeException("Lỗi Supabase Storage: Mã lỗi HTTP " + clientResponse.statusCode().value())))
                    .toBodilessEntity()
                    .block();

            return supabaseUrl + STORAGE_API_PATH + "public/" + bucketName + "/" + pathInStorage;

        } catch (Exception e) {

            throw new RuntimeException("Không thể tải file lên Supabase: " + e.getMessage(), e);
        }
    }


    public void deleteFile(String oldPublicUrl) {
        if (oldPublicUrl == null || oldPublicUrl.isEmpty()) {
            return;
        }


        if (!oldPublicUrl.startsWith(supabaseUrl)) {
            System.err.println("Cảnh báo: URL công khai không hợp lệ, không phải từ dự án Supabase này: " + oldPublicUrl);
            return;
        }

        String pathPrefix = STORAGE_API_PATH + "public/" + bucketName + "/";
        int startIndex = oldPublicUrl.indexOf(pathPrefix);

        if (startIndex == -1) {
            System.out.println("Cảnh báo: Không thể phân tích cú pháp URL Supabase: " + oldPublicUrl);
            return;
        }


        String pathInBucket = oldPublicUrl.substring(startIndex + pathPrefix.length());


        String fullDeleteUrl = supabaseUrl + STORAGE_API_PATH + bucketName + "/" + pathInBucket;

        try {
            webClient.delete()
                    .uri(fullDeleteUrl)
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .retrieve().onStatus(status -> status.isError(), clientResponse ->
                            Mono.error(new RuntimeException("Lỗi Supabase Storage: Mã lỗi HTTP " + clientResponse.statusCode().value())))
                    .toBodilessEntity()
                    .block();

        } catch (Exception e) {

            System.err.println("Lỗi khi xóa file cũ từ Supabase (" + oldPublicUrl + "): " + e.getMessage());
        }
    }
    public String getMimeTypeFromUrl(String fileUrl) {
        try {

            String mimeType = webClient.head()
                    .uri(fileUrl)
                    .retrieve()
                    .toBodilessEntity()
                    .block()
                    .getHeaders()
                    .getContentType()
                    .toString();

            if (mimeType == null || mimeType.isEmpty()) {
                throw new RuntimeException("Content-Type header is missing.");
            }
            return mimeType;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve Content-Type from URL: " + fileUrl + ". Hãy đảm bảo file ở chế độ PUBLIC.", e);
        }
    }


    public String getPublicUrl(String objectPath) {

        return supabaseUrl + STORAGE_API_PATH + "public/" + bucketName + "/" + objectPath;
    }


    public String updateFile(MultipartFile newFile, String oldPublicUrl, String folderPath) {
        deleteFile(oldPublicUrl);


        return uploadNewFile(newFile, folderPath);
    }
}