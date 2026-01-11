package com.example.Automated.Application.Mangament.serviceImplements;


import com.google.genai.Client;
import com.google.genai.errors.ApiException;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part; // Sử dụng Part.fromBytes
import com.google.genai.types.GenerateContentConfig;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.model.name}")
    private String modelName;

    private final Client geminiClient;
    private final WebClient fileDownloaderWebClient;

    public GeminiService(Client geminiClient,
                         @Qualifier("fileDownloaderWebClient") WebClient.Builder webClientBuilder) {

        this.geminiClient = geminiClient;
        this.fileDownloaderWebClient = webClientBuilder.build();
    }



    private byte[] downloadFile(String fileUrl) {
        try {
            byte[] fileBytes = fileDownloaderWebClient.get()
                    .uri(fileUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            if (fileBytes == null || fileBytes.length == 0) {
                throw new RuntimeException("File tải về bị rỗng.");
            }
            return fileBytes;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tải file từ URL: " + fileUrl, e);
        }
    }



    public String extractDataFromDocument(String fileUrl, String mimeType, String extractionPrompt) throws Exception {


        byte[] fileBytes = downloadFile(fileUrl);


        Part textPart = Part.fromText(extractionPrompt);


        Part dataPart = Part.fromBytes(fileBytes, mimeType);


        Content content = Content.fromParts(textPart, dataPart);
      try {
            GenerateContentResponse response = geminiClient.models.generateContent(
                    modelName,
                    content,
                    null
            );


            return response.text();

        } catch (ApiException e) {
            throw new RuntimeException("Lỗi gọi Gemini API: " + e.getMessage(), e);
        } catch (Exception e) {
            throw e;
        }
    }

    public String autoEvaluateSubmission(String evaluationPrompt) throws Exception {


        Part textPart = Part.fromText(evaluationPrompt);
        Content content = Content.fromParts(textPart);

        try {

            GenerateContentResponse response = geminiClient.models.generateContent(
                    modelName,
                    content,
                    null
            );


            return response.text();

        } catch (ApiException e) {
            throw new RuntimeException("Lỗi gọi Gemini API trong quá trình duyệt tự động: " + e.getMessage(), e);
        } catch (Exception e) {
            throw e;
        }
    }
}