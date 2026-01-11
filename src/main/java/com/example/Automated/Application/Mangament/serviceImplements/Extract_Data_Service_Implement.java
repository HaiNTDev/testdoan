package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.model.DocumentRule;
import com.example.Automated.Application.Mangament.model.Extract_Data_Trainee_Document;
import com.example.Automated.Application.Mangament.model.Trainee_Document_Submission;
import com.example.Automated.Application.Mangament.repositories.Extract_Data_Trainee_Document_Repository;
import com.example.Automated.Application.Mangament.repositories.TraineeSubmissionRepository;
import com.example.Automated.Application.Mangament.serviceInterfaces.SupabaseStorageServiceInterface;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class Extract_Data_Service_Implement {
    @Autowired
    private SupabaseStorageServiceInterface supabaseStorageServiceInterface;

    @Autowired
    private TraineeSubmissionRepository traineeSubmissionRepository;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private Extract_Data_Trainee_Document_Repository extractDataTraineeDocumentRepository;

//    @Async
//    @Transactional
    public void extractDataForSubmission(Trainee_Document_Submission submission) throws Exception {
        String rawFilePath = submission.getFilePath();
        if (rawFilePath == null || rawFilePath.isBlank()) return;

        String[] uploadUrls = rawFilePath.split(";;");
        final String TARGET_LANGUAGE = "Vietnamese";
        ObjectMapper objectMapper = new ObjectMapper();

        List<String> ruleNames = submission.getDocument().getDocumentRuleList()
                .stream()
                .map(DocumentRule::getDocumentRuleName)
                .toList();

        Map<String, String> schema = new HashMap<>();
        ruleNames.forEach(name -> schema.put(name, ""));
        String jsonSchema = objectMapper.writeValueAsString(schema);


        for (String fileUrl : uploadUrls) {
            if (fileUrl.isBlank()) continue;

            try {
                String mimeType = supabaseStorageServiceInterface.getMimeTypeFromUrl(fileUrl);

                String finalPrompt = String.format(
                        "Bạn là một chuyên gia trích xuất dữ liệu từ tài liệu định danh và bằng cấp. \n" +
                                "Tôi sẽ cung cấp cho bạn một danh sách cấu hình dưới dạng JSON, trong đó: \n" +
                                "- 'KEY' (Tên Rule): Là từ khóa hoặc nhãn (Label) bạn cần tìm trên tài liệu. \n" +
                                "- 'VALUE' (Chỉ tiêu): Là yêu cầu cụ thể về cách bạn phải lấy dữ liệu cho từ khóa đó. \n\n" +
                                "Cấu hình cần trích xuất: %s \n\n" +
                                "### CÁC QUY TẮC BẮT BUỘC: ### \n" +
                                "1. ĐỐI CHIẾU NGỮ NGHĨA (SEMANTIC MATCHING): \n" +
                                "   - Nếu tài liệu là tiếng Anh, hãy tự động dịch 'KEY' sang tiếng Anh tương đương để tìm. \n" +
                                "   - Ví dụ: 'Họ và Tên' tương đương 'Full Name', 'Tổng điểm' tương đương 'Total Score', 'Ngày sinh' tương đương 'Date of birth'. \n" +
                                "2. TUÂN THỦ CHỈ TIÊU (VALUE): \n" +
                                "   - Dựa vào 'VALUE' để trích xuất đúng định dạng (Ví dụ: 'đầy đủ', 'chỉ lấy số', hoặc 'viết hoa'). \n" +
                                "3. CHỈ LẤY THÔNG TIN CHỦ SỞ HỮU (CARDHOLDER ONLY): \n" +
                                "   - CHỈ trích xuất thông tin của người sở hữu tài liệu. \n" +
                                "   - TUYỆT ĐỐI KHÔNG lấy tên các cán bộ ký tên, cục trưởng, hoặc cơ quan cấp phát. \n" +
                                "   - CẢNH BÁO: Bỏ qua các tên nằm dưới các chức danh như 'Cục trưởng', 'Director', 'Giám đốc' hoặc nằm gần con dấu (Ví dụ: Không lấy tên 'Tô Văn Huệ'). \n" +
                                "4. VÙNG MRZ VÀ CHỮ NHIỄU: \n" +
                                "   - Không trích xuất dữ liệu từ 3 dòng ký tự lạ (MRZ) ở dưới cùng mặt sau CCCD. \n" +
                                "5. NẾU KHÔNG TÌM THẤY: \n" +
                                "   - Nếu một 'KEY' không có nhãn tương ứng xuất hiện trên mặt này của ảnh, hãy trả về giá trị rỗng \"\". \n\n" +
                                "Chỉ trả về duy nhất 1 đối tượng JSON sạch. Ngôn ngữ kết quả: %s.",
                        jsonSchema, TARGET_LANGUAGE
                );

                String jsonResponse = geminiService.extractDataFromDocument(fileUrl, mimeType, finalPrompt);


                String cleanJson = jsonResponse.trim();if (cleanJson.startsWith("```")) {

                    cleanJson = cleanJson.replaceAll("^```json|```$", "").trim();
                }

                Map<String, String> extractedData = objectMapper.readValue(cleanJson, new TypeReference<Map<String, String>>() {});

                for (Map.Entry<String, String> entry : extractedData.entrySet()) {
                    String ruleName = entry.getKey();
                    String value = entry.getValue();


                    if (value != null && !value.trim().isEmpty() && !value.equalsIgnoreCase("N/A")) {
                        System.out.println("Tìm thấy dữ liệu tại file [" + fileUrl + "] cho Rule [" + ruleName + "]: " + value);

                        saveExtractedData(ruleName, value, submission);
                    }
                }

            } catch (Exception e) {
                System.err.println("Lỗi xử lý file: " + fileUrl + " - " + e.getMessage());

            }
        }
    }

    @Transactional
    public boolean deleteExtractedDataBySubmission(Trainee_Document_Submission submission){
        long deletedCount = extractDataTraineeDocumentRepository.deleteByTraineeDocumentSubmission(submission);

        System.out.println("Đã xóa " + deletedCount + " bản ghi dữ liệu trích xuất cũ.");

        return deletedCount > 0;
    }

    private void saveExtractedData(String data_name, String extractedValue, Trainee_Document_Submission submission) {
        Extract_Data_Trainee_Document extractedEntity = new Extract_Data_Trainee_Document();
        extractedEntity.setData_name(data_name);
        extractedEntity.setData(extractedValue);
        extractedEntity.setTraineeDocumentSubmission(submission);
        extractedEntity.setCreateAt(LocalDateTime.now());
        extractedEntity.setActive(true);
        extractDataTraineeDocumentRepository.save(extractedEntity);
    }
}
