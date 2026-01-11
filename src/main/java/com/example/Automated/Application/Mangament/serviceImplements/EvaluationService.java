    package com.example.Automated.Application.Mangament.serviceImplements;



    import com.example.Automated.Application.Mangament.dto.request.FullEvaluationReport;
    import com.example.Automated.Application.Mangament.dto.request.RuleCriteria;
    import com.example.Automated.Application.Mangament.dto.request.RuleEvaluation;
    import com.example.Automated.Application.Mangament.serviceImplements.GeminiService; // SỬ DỤNG CỦA BẠN
    import com.fasterxml.jackson.core.type.TypeReference;
    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
    import java.util.List;
    import java.util.Map;

    @Service
    public class EvaluationService {

        private final ObjectMapper objectMapper;
        private final GeminiService geminiService;

        public EvaluationService(ObjectMapper objectMapper, GeminiService geminiService) {
            this.objectMapper = objectMapper;
            this.geminiService = geminiService;
        }

        public FullEvaluationReport evaluateTraineeSubmission(
                List<RuleCriteria> rules,
                Map<String, Object> extracData
        ) throws Exception {

            String rulesJson = objectMapper.writeValueAsString(rules);
            String candidateJson = objectMapper.writeValueAsString(extracData);

            String promptContent = String.format("""
                Bạn là một hệ thống đánh giá hồ sơ chuyên nghiệp.
                Dựa trên các quy tắc bắt buộc (REQUIRED_RULES) và dữ liệu ứng viên (CANDIDATE_DATA), hãy đánh giá từng tiêu chí.
                
                **REQUIRED_RULES** (Tiêu chí bắt buộc):
                %s
                
                **CANDIDATE_DATA** (Dữ liệu đã trích xuất):
                %s
                
                **QUY TẮC ĐẦU RA:**
                1. ĐẦU RA BẮT BUỘC CHỈ LÀ JSON (Không có văn bản mô tả).
                2. Sử dụng định dạng JSON đã định nghĩa.
                3. Trường "EVALUATED_RULES" phải là một mảng, ngay cả khi rỗng, TUYỆT ĐỐI KHÔNG ĐƯỢC ĐỂ NULL.
                4. Trạng thái chung (OVERALL_STATUS) là "APPROVED" nếu tất cả quy tắc PASS, ngược lại là "REJECTED".
                
                HÃY TRẢ VỀ KẾT QUẢ ĐÁNH GIÁ CUỐI CÙNG Ở ĐỊNH DẠNG JSON.
                """,
                    rulesJson,
                    candidateJson
            );

            String resultText = geminiService.autoEvaluateSubmission(promptContent);


            if (resultText == null || resultText.trim().isEmpty()) {
                throw new RuntimeException("Gemini API returned an empty or null response.");
            }

            System.out.println("\n---------------------------------------------------------");
            System.out.println("API RESPONSE THÔ từ Submission ID (Dữ liệu chính): " + extracData.get("id"));
            System.out.println(resultText);
            System.out.println("---------------------------------------------------------");



            String cleanJson = resultText
                    .replaceAll("```json|```", "")
                    .replaceAll("[\r\n\t]", "")
                    .trim();

            try {

                JsonNode rootNode = objectMapper.readTree(cleanJson);


                String overallStatus = rootNode.get("OVERALL_STATUS").asText();


                JsonNode rulesNode = rootNode.get("EVALUATED_RULES");


                List<RuleEvaluation> evaluatedRules;
                if (rulesNode != null && rulesNode.isArray()) {
                    evaluatedRules = objectMapper.readValue(
                            rulesNode.traverse(),
                            new TypeReference<List<RuleEvaluation>>() {}
                    );
                } else {
                    evaluatedRules = List.of();
                }


                FullEvaluationReport report = new FullEvaluationReport();
                report.setOVERALL_STATUS(overallStatus);
                report.setEVALUATED_RULES(evaluatedRules);


                return report;

            } catch (Exception jsonEx) {


                System.err.println("KHÔNG THỂ PHÂN TÍCH JSON. CHUỖI GÂY LỖI:");
                System.err.println(cleanJson);
                System.err.println("LỖI JACKSON CHI TIẾT: " + jsonEx.getMessage());
                System.err.println("---------------------------------------------------------");

                throw new RuntimeException("Failed to parse JSON response from Gemini. Check raw data for errors: " + jsonEx.getMessage(), jsonEx);
            }

        }
    }