package com.example.Automated.Application.Mangament.serviceImplements;


import com.example.Automated.Application.Mangament.dto.response.DocumentRuleValueCellResponse;
import com.example.Automated.Application.Mangament.dto.response.Extract_Data_Response;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.dto.response.TraineeSubmissionDetailResponse;
import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.example.Automated.Application.Mangament.model.*;
import com.example.Automated.Application.Mangament.repositories.*;
import com.example.Automated.Application.Mangament.serviceInterfaces.SupabaseStorageServiceInterface;
import com.example.Automated.Application.Mangament.serviceInterfaces.TraineeSubmissionServiceInterface;
import com.example.Automated.Application.Mangament.utils.AuthenUntil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TraineeSubmissionServiceImplement implements TraineeSubmissionServiceInterface {
    private static final List<StatusEnum> BLOCKING_STATUSES =
            List.of(StatusEnum.Pending, StatusEnum.InProgress, StatusEnum.Approve, StatusEnum.Reject, StatusEnum.Complete);

    private static final List<StatusEnum> LOCKED_UPDATE_STATUSES =
            List.of(StatusEnum.Approve, StatusEnum.Complete);

    @Autowired
    private InputDocumentMatrixRepository inputDocumentMatrixRepository;

    @Autowired
    private Extract_Data_Service_Implement extractDataService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private Extract_Data_Trainee_Document_Repository extractDataTraineeDocumentRepository;

    @Autowired
    private TraineeApplicationRepository traineeApplicationRepository;

    @Autowired
    TraineeSubmissionRepository traineeSubmissionRepository;

    @Autowired
    SupabaseStorageServiceInterface supabaseStorageServiceInterface;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private AuthenUntil authenUntil;

    public ResponseEntity<ResponseObj> getTraineeSubmissionDetail(long traineeSubmissionId) {
        try {
            Optional<Trainee_Document_Submission> traineeDocumentSubmissionOpt = traineeSubmissionRepository.findById(traineeSubmissionId);

            if (traineeDocumentSubmissionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(
                        HttpStatus.NOT_FOUND.toString(),
                        "Trainee submission does not exist.",
                        null
                ));
            }

            Trainee_Document_Submission traineeDocumentSubmission = traineeDocumentSubmissionOpt.get();
            TraineeSubmissionDetailResponse detailResponse = convertTraineeSubmissionResponse(traineeDocumentSubmission);

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                    HttpStatus.OK.toString(),
                    "Trainee Submission detail retrieved successfully.",
                    detailResponse
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    e.getMessage(),
                    null
            ));
        }
    }

    private TraineeSubmissionDetailResponse convertTraineeSubmissionResponse(Trainee_Document_Submission traineeDocumentSubmission) {
        TraineeSubmissionDetailResponse traineeSubmissionDetailResponse = new TraineeSubmissionDetailResponse();

        traineeSubmissionDetailResponse.setSubmissionId(traineeDocumentSubmission.getId());


        if (traineeDocumentSubmission.getDocument() != null) {
            traineeSubmissionDetailResponse.setDocument_id(traineeDocumentSubmission.getDocument().getId());
            traineeSubmissionDetailResponse.setRequiredDocumentName(traineeDocumentSubmission.getDocument().getDocumentName());
        } else {
            traineeSubmissionDetailResponse.setDocument_id(0);
            traineeSubmissionDetailResponse.setRequiredDocumentName(null);
        }

        String[] uploadUrl = traineeDocumentSubmission.getFilePath().split(";;");
        List<String> fileUpload = new ArrayList<>();
        for(int i = 0; i < uploadUrl.length; i++){
            fileUpload.add(uploadUrl[i]);
        }
        traineeSubmissionDetailResponse.setSubmissionStatus(traineeDocumentSubmission.getStatusEnum() != null ? traineeDocumentSubmission.getStatusEnum().name() : null);
        traineeSubmissionDetailResponse.setSubmission_name(traineeDocumentSubmission.getTrainee_document_name());
        traineeSubmissionDetailResponse.setTakeNote(traineeDocumentSubmission.getTake_note());
        traineeSubmissionDetailResponse.setReport(traineeDocumentSubmission.getReport());
        traineeSubmissionDetailResponse.setFileDownloadUrl(fileUpload);
        traineeSubmissionDetailResponse.setUploadTime(traineeDocumentSubmission.getCreateAt());
//
//        if(!traineeDocumentSubmission.getExtractDataTraineeDocumentList().isEmpty()){
//            for(Extract_Data_Trainee_Document extractDataTraineeDocument : traineeDocumentSubmission.getExtractDataTraineeDocumentList()){
//                Extract_Data_Response extractDataResponse = new Extract_Data_Response();
//                extractDataResponse.setExtract_data_id(extractDataTraineeDocument.getId());
//                extractDataResponse.setExtract_data_name(extractDataTraineeDocument.getData_name());
//                extractDataResponse.setExtract_Data_value(extractDataTraineeDocument.getData());
//            }
//        }
//        List<InputDocumentMatrix>  inputDocumentMatrixList = inputDocumentMatrixRepository.findAll();
//        InputDocumentMatrix inputDocumentMatrix = new InputDocumentMatrix();
//        for(InputDocumentMatrix inputDocumentMatrix1 : inputDocumentMatrixList){
//            if(inputDocumentMatrix1.getDocument().getId() == traineeDocumentSubmission.getDocument().getId() && inputDocumentMatrix1.getPosition().getId() == traineeDocumentSubmission.getTraineeApplication().getPosition().getId()){
//                inputDocumentMatrix = inputDocumentMatrix1;
//                break;
//            }
//        }
//
//        if(!inputDocumentMatrix.getDocumentRuleValueList().isEmpty()){
//            for(DocumentRuleValue documentRuleValue : inputDocumentMatrix.getDocumentRuleValueList()){
//                DocumentRuleValueCellResponse documentRuleValueCellResponse = new DocumentRuleValueCellResponse();
//                documentRuleValueCellResponse.setDocument_rule_id(documentRuleValue.getDocumentRule().getId());
//                documentRuleValueCellResponse.setDocument_rule_name(documentRuleValue.getDocumentRule().getDocumentRuleName());
//                documentRuleValueCellResponse.setDocument_rule_value_id(documentRuleValue.getId());
//                documentRuleValueCellResponse.setValue(documentRuleValue.getRuleValue());
//            }
//        }

        return traineeSubmissionDetailResponse;
    }

    public ResponseEntity<ResponseObj> createTraineeSubmission(long documentID, long trainee_application_id, String submission_name, String take_note, List<MultipartFile> submission_document_file) {
        try {
//            LocalDateTime now = LocalDateTime.now();
//            Optional<Batch> activeBatch = batchRepository.findAll().stream()
//                    .filter(Batch::isActive)
//                    .findFirst();
//
//            if (activeBatch.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObj(
//                        "BATCH_CLOSED",
//                        "Hiện tại không có đợt nộp hồ sơ nào được mở.",
//                        null));
//            }
//
//            if (now.isBefore(activeBatch.get().getStartDate()) || now.isAfter(activeBatch.get().getEndDate())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObj(
//                        "OUT_OF_BATCH_TIME",
//                        "Thời gian nộp hồ sơ không nằm trong khung giờ quy định của hệ thống.",
//                        null));
//            }

            Optional<Document> document = documentRepository.findById(documentID);
            Optional<TraineeApplication> traineeApplication = traineeApplicationRepository.findById(trainee_application_id);

            if (document.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), "Document does not exist in this trainee application.", null));
            }

            if (traineeApplication.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseObj("Create submission fail", "Trainee Application does not exist.", null));
            }

            boolean existingBlockingSubmission = traineeSubmissionRepository.existsByDocumentIdAndTraineeApplicationIdAndStatusEnumIn(
                    documentID,
                    trainee_application_id,
                    BLOCKING_STATUSES
            );

            boolean flag = false;
            for (Trainee_Document_Submission traineeDocumentSubmission : traineeApplication.get().getTraineeDocumentSubmissionList()) {
                if (traineeDocumentSubmission.getDocument() != null && traineeDocumentSubmission.getDocument().getId() == documentID) {
                    flag = true;
                    break;
                }
            }

            if (flag && existingBlockingSubmission) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObj("Create submission fail", "A submission for this document is already pending, approved, or completed.", null));
            }

            if (existingBlockingSubmission) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObj("Create submission fail", "Cannot create new submission. An active submission for this document is already pending or approved.", null));
            }

            if (submission_name == null || submission_name.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Submission name should not be blank", null));
            }

            String documentName = document.get().getDocumentName().trim().toLowerCase();
            String submittedName = submission_name.trim().toLowerCase();
            if (!documentName.equals(submittedName)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Submission name must be the same as the document name: " + document.get().getDocumentName(), null));
            }

            if (take_note == null || take_note.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Take note should not be blank", null));
            }

            if (submission_document_file == null || submission_document_file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Submission file should not be null", null));
            }


            String folderPath = "application_" + trainee_application_id + "/document_" + documentID;
            List<String> uploadedUrls = new ArrayList<>();

            try {
                for (MultipartFile multipartFile : submission_document_file) {
                    if (!multipartFile.isEmpty()) {
                        String url = supabaseStorageServiceInterface.uploadNewFile(multipartFile, folderPath);
                        uploadedUrls.add(url);
                    }
                }
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseObj("Create submission fail", "Error uploading file to storage service: " + e.getMessage(), null));
            }

            String filePath = "";
            String filePath2 = "";
            if (uploadedUrls.size() > 1) {
                for (int i = 0; i < uploadedUrls.size(); i++) {
                    if (i == uploadedUrls.size() - 1) {
                        filePath = uploadedUrls.get(i);
                        filePath2 += filePath;
                    } else {
                        filePath = uploadedUrls.get(i) + ";;";
                        filePath2 += filePath;

                    }
                }
            }

            if (uploadedUrls.size() == 1) {
                for (String s : uploadedUrls) {
                    filePath2 = s;
                }
            }

//
//            String finalCombinedPath = String.join(";;", uploadedUrls);
//
//            System.out.println("Final Path Saved to DB: " + finalCombinedPath);


            Trainee_Document_Submission traineeDocumentSubmission = new Trainee_Document_Submission();
            traineeDocumentSubmission.setFilePath(filePath2);
            traineeDocumentSubmission.setTrainee_document_name(submission_name.trim());
            traineeDocumentSubmission.setTake_note(take_note.trim());
            traineeDocumentSubmission.setActive(true);
            traineeDocumentSubmission.setTraineeApplication(traineeApplication.get());
            traineeDocumentSubmission.setDocument(document.get());
            traineeDocumentSubmission.setStatusEnum(StatusEnum.InProgress);
            traineeDocumentSubmission.setReport("WAITING_FOR_AI_EXTRACTION");
            traineeDocumentSubmission.setCreateAt(LocalDateTime.now());
            traineeSubmissionRepository.save(traineeDocumentSubmission);

            Trainee_Document_Submission savedSubmission = traineeDocumentSubmission;

            try {
                Optional<InputDocumentMatrix> result = inputDocumentMatrixRepository.findAll().stream()
                        .filter(matrix ->
                                matrix.getDocument().getId() == documentID &&
                                        matrix.getPosition().getId() == traineeApplication.get().getPosition().getId()
                        )
                        .findFirst();

                if (!result.get().getDocumentRuleValueList().isEmpty()) {
                    extractDataService.extractDataForSubmission(savedSubmission);
                    traineeDocumentSubmission.setReport("AI_COMPLETED");
                    traineeSubmissionRepository.save(traineeDocumentSubmission);
                    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObj(
                            HttpStatus.CREATED.toString(),
                            "Create Submission successful and data extraction started.",
                            null
                    ));
                }else{

                    traineeSubmissionRepository.save(traineeDocumentSubmission);
                    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObj(HttpStatus.CREATED.toString(), "Create submission successfull", null));
                }
            } catch (Exception e) {
                System.err.println("Lỗi tự động trích xuất: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                        "EXTRACTION_FAILED",
                        "Submission created but automatic data extraction failed: " + e.getMessage(),
                        null
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObj> updateTraineeSubmission(
            long submissionID,
            String new_submission_name,
            String new_take_note,
            List<MultipartFile> new_submission_document_files) {

        try {

//            LocalDateTime now = LocalDateTime.now();
//            Optional<Batch> activeBatch = batchRepository.findAll().stream()
//                    .filter(Batch::isActive)
//                    .findFirst();
//
//            if (activeBatch.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObj(
//                        "BATCH_CLOSED",
//                        "Không thể cập nhật hồ sơ vì đợt nộp đã đóng.",
//                        null));
//            }
//
//            if (now.isBefore(activeBatch.get().getStartDate()) || now.isAfter(activeBatch.get().getEndDate())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObj(
//                        "OUT_OF_BATCH_TIME",
//                        "Đã hết thời gian cho phép chỉnh sửa hồ sơ trong đợt này.",
//                        null));
//            }

            Optional<Trainee_Document_Submission> existingSubmissionOpt = traineeSubmissionRepository.findById(submissionID);

            if (!existingSubmissionOpt.isPresent()) {
                return ResponseEntity.badRequest().body(new ResponseObj("Update fail", "Submission not found.", null));
            }

            Trainee_Document_Submission existingSubmission = existingSubmissionOpt.get();

            if (LOCKED_UPDATE_STATUSES.contains(existingSubmission.getStatusEnum())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObj("Fail", "Approved/Locked.", null));
            }

            String finalPublicUrl = existingSubmission.getFilePath();
            boolean fileWasUpdated = new_submission_document_files != null && !new_submission_document_files.isEmpty();

            if (fileWasUpdated) {
                String folderPath = "application_" + existingSubmission.getTraineeApplication().getId()
                        + "/document_" + existingSubmission.getDocument().getId();

                List<String> uploadUrls = new ArrayList<>();
                for (MultipartFile file : new_submission_document_files) {
                    if (!file.isEmpty()) {
                        uploadUrls.add(supabaseStorageServiceInterface.uploadNewFile(file, folderPath));
                    }
                }
                finalPublicUrl = String.join(";;", uploadUrls);
            }


            existingSubmission.setFilePath(finalPublicUrl);
            existingSubmission.setTrainee_document_name(new_submission_name.trim());
            existingSubmission.setTake_note(new_take_note.trim());
            existingSubmission.setUpdateAt(LocalDateTime.now());
            existingSubmission.setStatusEnum(StatusEnum.InProgress);


            existingSubmission.setReport("WAITING_FOR_AI_EXTRACTION");
            existingSubmission.setReject_reason(null);
            traineeSubmissionRepository.saveAndFlush(existingSubmission);

            if (fileWasUpdated) {
                try {

                    Optional<InputDocumentMatrix> matrixOpt = inputDocumentMatrixRepository.findAll().stream()
                            .filter(m -> m.getDocument().getId() == existingSubmission.getDocument().getId() &&
                                    m.getPosition().getId() == existingSubmission.getTraineeApplication().getPosition().getId())
                            .findFirst();

                    if (matrixOpt.isPresent() && !matrixOpt.get().getDocumentRuleValueList().isEmpty()) {


                        extractDataTraineeDocumentRepository.deleteBySubmissionIdNative(existingSubmission.getId());
          if (existingSubmission.getExtractDataTraineeDocumentList() != null) {
                            existingSubmission.getExtractDataTraineeDocumentList().clear();
                        }


                           extractDataService.extractDataForSubmission(existingSubmission);
                        existingSubmission.setReport("AI_COMPLETED");
                        traineeSubmissionRepository.saveAndFlush(existingSubmission);

                        return ResponseEntity.ok().body(new ResponseObj(
                                HttpStatus.OK.toString(),
                                "Cập nhật thành công. AI đang trích xuất lại dữ liệu...",
                                null
                        ));
                    }
                } catch (Exception e) {
                    return ResponseEntity.status(500).body(new ResponseObj("AI_START_ERROR", e.getMessage(), null));
                }
            }

            traineeSubmissionRepository.saveAndFlush(existingSubmission);
            return ResponseEntity.ok().body(new ResponseObj(HttpStatus.OK.toString(), "Cập nhật thành công!", null));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseObj("ERROR", e.getMessage(), null));
        }
    }
}