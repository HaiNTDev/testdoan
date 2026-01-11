package com.example.Automated.Application.Mangament.serviceImplements;



import com.example.Automated.Application.Mangament.dto.request.FullEvaluationReport;
import com.example.Automated.Application.Mangament.dto.request.RuleCriteria;
import com.example.Automated.Application.Mangament.dto.request.RuleEvaluation;
import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.example.Automated.Application.Mangament.model.Extract_Data_Trainee_Document;
import com.example.Automated.Application.Mangament.model.InputDocumentMatrix;
import com.example.Automated.Application.Mangament.model.TraineeApplication;
import com.example.Automated.Application.Mangament.model.Trainee_Document_Submission;
import com.example.Automated.Application.Mangament.repositories.Extract_Data_Trainee_Document_Repository;
import com.example.Automated.Application.Mangament.repositories.InputDocumentMatrixRepository;
import com.example.Automated.Application.Mangament.repositories.TraineeApplicationRepository;
import com.example.Automated.Application.Mangament.repositories.TraineeSubmissionRepository;
import com.example.Automated.Application.Mangament.serviceInterfaces.TraineeApplicationServiceInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class AutoEvaluationService {
    @Autowired
    private TraineeApplicationServiceInterface traineeApplicationServiceInterface;
    private final TraineeApplicationRepository traineeApplicationRepository;
    private final TraineeSubmissionRepository traineeSubmissionRepository;
    private final PositionRuleService positionRuleService;
    private final EvaluationService evaluationService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private InputDocumentMatrixRepository inputDocumentMatrixRepository;

    @Autowired
    private Extract_Data_Trainee_Document_Repository extractDataRepository;

    public AutoEvaluationService(
            TraineeApplicationRepository traineeApplicationRepository,
            TraineeSubmissionRepository traineeSubmissionRepository,
            PositionRuleService positionRuleService,
            EvaluationService evaluationService) {
        this.traineeApplicationRepository = traineeApplicationRepository;
        this.traineeSubmissionRepository = traineeSubmissionRepository;
        this.positionRuleService = positionRuleService;
        this.evaluationService = evaluationService;
    }

    @Scheduled(fixedRate = 2000)
    @Transactional
    public void setRejectReasonInputMatrix() {
        System.out.println("Starting scheduled job: setRejectReasonInputMatrix");

        List<InputDocumentMatrix> matrixList = inputDocumentMatrixRepository.findAll();


        List<InputDocumentMatrix> matricesToUpdate = matrixList.stream()

                .filter(matrix -> matrix.getStatusEnum() == StatusEnum.Approve ||
                        matrix.getStatusEnum() == StatusEnum.Complete)

                .filter(matrix -> matrix.getRejection_reason() != null)
                .peek(matrix -> {

                    matrix.setRejection_reason(null);
                })
                .collect(Collectors.toList());


        if (!matricesToUpdate.isEmpty()) {
            inputDocumentMatrixRepository.saveAll(matricesToUpdate);
            System.out.printf("Successfully cleared reject reasons for %d matrix entries.\n", matricesToUpdate.size());
        } else {
            System.out.println("No matrix entries found that require clearing reject reasons.");
        }
    }


    @Scheduled(fixedRate = 5000)
    @Transactional
    public void runAutoEvaluationJob() {
        System.out.println("--- Bắt đầu Job Kiểm tra duyệt tự động ---");


        List<TraineeApplication> applications = traineeApplicationRepository.findByStatusEnum(StatusEnum.InProgress);

        for (TraineeApplication app : applications) {
            for (Trainee_Document_Submission submission : app.getTraineeDocumentSubmissionList()) {

                if (submission.getStatusEnum() != StatusEnum.InProgress) continue;
                String currentReport = "";
                if (submission.getReport() != null) {
                    currentReport = submission.getReport().replace("\"", "").trim();
                }

                if (!"AI_COMPLETED".equalsIgnoreCase(currentReport)) {
                    if ("WAITING_FOR_AI_EXTRACTION".equalsIgnoreCase(currentReport)) {
                        System.out.println(">>> [ĐỢI AI] Submission ID " + submission.getId() + " chưa xong AI...");
                    }
                    continue;
                }

                try {
                    entityManager.refresh(submission);

                    List<Extract_Data_Trainee_Document> dbData = extractDataRepository.findByTraineeDocumentSubmission(submission);

                    if (dbData == null || dbData.isEmpty()) {
                        submission.setStatusEnum(StatusEnum.Approve);
                        traineeSubmissionRepository.save(submission);
                    }

                    System.out.println(">>> [TIẾN HÀNH DUYỆT] Đang chấm điểm cho Submission ID: " + submission.getId());

                    long positionId = app.getPosition().getId();
                    long documentId = submission.getDocument().getId();
                    List<RuleCriteria> specificRules = positionRuleService.getRulesByPositionAndDocument(positionId, documentId);

                    Map<String, Object> extractDataMap = dbData.stream()
                            .collect(Collectors.toMap(
                                    Extract_Data_Trainee_Document::getData_name,
                                    d -> d.getData() == null ? "" : d.getData(),
                                    (existing, replacement) -> existing
                            ));

                    FullEvaluationReport reportResult = evaluationService.evaluateTraineeSubmission(specificRules, extractDataMap);

                    updateSubmissionStatus(submission, reportResult);
//
//                    traineeSubmissionRepository.save(submission);

                } catch (Exception e) {
                    System.err.println("Lỗi xử lý chấm điểm submission " + submission.getId() + ": " + e.getMessage());
                }
            }
        }
    }
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void runStatusSynchronizationJob() {

        System.out.println("--- Bắt đầu Job Đồng bộ Trạng thái Application ---");

        try {

            List<TraineeApplication> applications =
                    traineeApplicationRepository.findAll();

            System.out.println("Tìm thấy " + applications.size() + " Application cần đồng bộ.");

            for (TraineeApplication app : applications) {
                try {

                    double progress = traineeApplicationServiceInterface
                            .calculateApplicationProgressAndSyncStatus(app);

                    System.out.println("Application ID " + app.getId() +
                            " - Tiến độ: " + String.format("%.2f", progress) +
                            "%. Trạng thái mới: " + app.getStatusEnum().name());

                } catch (Exception syncEx) {
                    System.err.println("Lỗi đồng bộ trạng thái cho Application ID = " + app.getId());
                    syncEx.printStackTrace();
                }
            }
        } catch (Exception jobEx) {
            System.err.println(" LỖI NGHIÊM TRỌNG TRONG JOB ĐỒNG BỘ TRẠNG THÁI!");
            jobEx.printStackTrace();
        }

        System.out.println("--- Kết thúc Job Đồng bộ Trạng thái ---");
    }


    private String formatRulesToPrettyString(List<RuleEvaluation> rules, boolean isFullReport) {
        if (rules == null || rules.isEmpty()) return "Không có dữ liệu.";

        StringBuilder sb = new StringBuilder();
        for (RuleEvaluation rule : rules) {
            if (isFullReport || "FAIL".equalsIgnoreCase(rule.getStatus())) {
                String icon = "PASS".equalsIgnoreCase(rule.getStatus()) ? "✅" : "❌";

                sb.append(icon).append(" ").append(rule.getRule_name()).append("\n");
                sb.append("   └─ Chi tiết: ").append(rule.getReason()).append("\n");
            }
        }
        return sb.toString();
    }

    private void updateSubmissionStatus(Trainee_Document_Submission submission, FullEvaluationReport report) {

        List<RuleEvaluation> rules = report.getEVALUATED_RULES();

        if ("APPROVED".equals(report.getOVERALL_STATUS())) {
            submission.setStatusEnum(StatusEnum.Approve);
            submission.setReject_reason(null);
            String successReport = formatRulesToPrettyString(rules, true);
            submission.setReport(successReport);
            System.out.println(">>> BÁO CÁO THÀNH CÔNG:\n" + successReport);

        } else {
            submission.setStatusEnum(StatusEnum.Reject);

            if (rules != null && !rules.isEmpty()) {

                String rejectionDetail = formatRulesToPrettyString(rules, false);
                submission.setReject_reason(rejectionDetail);
                submission.setReport(rejectionDetail);
            } else {
                submission.setReject_reason(" Lỗi: Không nhận được chi tiết đánh giá từ AI.");
            }
        }

        traineeSubmissionRepository.save(submission);
    }



}