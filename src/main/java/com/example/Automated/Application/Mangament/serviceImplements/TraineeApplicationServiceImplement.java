package com.example.Automated.Application.Mangament.serviceImplements;


import com.example.Automated.Application.Mangament.dto.response.*;
import com.example.Automated.Application.Mangament.enums.RoleEnum;
import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.example.Automated.Application.Mangament.model.*;
import com.example.Automated.Application.Mangament.repositories.*;
import com.example.Automated.Application.Mangament.serviceInterfaces.TraineeApplicationServiceInterface;
import com.example.Automated.Application.Mangament.utils.AuthenUntil;
import org.hibernate.annotations.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.stream.Collectors;

@Service
public class TraineeApplicationServiceImplement implements TraineeApplicationServiceInterface {
    @Autowired
    private TraineeApplicationRepository traineeApplicationRepository;

    //fix merge branch
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private  SupabaseStorageService supabaseService;

    @Autowired
    private  GeminiService geminiService;

    @Autowired
    private AccountPositionRepository accountPositionRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private TraineeSubmissionRepository traineeSubmissionRepository;

    @Autowired
    private InputDocumentMatrixRepository inputDocumentMatrixRepository;


    @Autowired
    private AuthenUntil authenUntil;

    @Async
    public void createDefaultApplication(Account account){
        TraineeApplication traineeApplication = new TraineeApplication();
        traineeApplication.setAccount(account);
        for(AccountPosition accountPosition : account.getAccountPositionList()){
            if(accountPosition.isActive()){
                traineeApplication.setPosition(accountPosition.getPosition());
                break;
            }
        }
        traineeApplication.setActive(true);
        traineeApplication.setCreateAt(LocalDateTime.now());
        traineeApplication.setStatusEnum(StatusEnum.InProgress);
        traineeApplicationRepository.save(traineeApplication);
    }

//    @Async
//    public void createDefaultTraineeSubmission(TraineeApplication traineeApplication){
//        Trainee_Document_Submission traineeDocumentSubmission = new Trainee_Document_Submission();
//        traineeDocumentSubmission.setTraineeApplication(traineeApplication);
//        traineeDocumentSubmission.setDocument(null);
//        traineeDocumentSubmission.setTrainee_document_name(null);
//        traineeDocumentSubmission.setStatusEnum(StatusEnum.Not_Yet);
//        traineeDocumentSubmission.setActive(true);
//        traineeDocumentSubmission.setTake_note(null);
//        traineeDocumentSubmission.setFilePath(null);
//        traineeSubmissionRepository.save(traineeDocumentSubmission);
//    }

    public String addPositiontoAccount(long accountId, long positionId){
        try{
            Optional<Account> account = accountRepository.findById(accountId);
            Optional<Position> position = positionRepository.findById(positionId);
            if(!position.isPresent()){
                return "Position id does not exist";
            }

            if(!account.isPresent()){
                return "Account id does not exist";
            }

            if(account.get().getAccountPositionList().isEmpty()){
                AccountPosition accountPosition = new AccountPosition();
                accountPosition.setPosition(position.get());
                accountPosition.setAccount(account.get());
                accountPosition.setActive(true);
                accountPosition.setCreateAt(LocalDateTime.now());
                account.get().getAccountPositionList().add(accountPosition);
                accountRepository.save(account.get());
                accountPositionRepository.save(accountPosition);
                return "Add succesfully";
            }else{
                for(AccountPosition accountPosition : account.get().getAccountPositionList()){
                    accountPosition.setActive(false);
                }

                AccountPosition accountPosition = new AccountPosition();
                accountPosition.setPosition(position.get());
                accountPosition.setAccount(account.get());
                accountPosition.setActive(true);
                accountPosition.setCreateAt(LocalDateTime.now());
                account.get().getAccountPositionList().add(accountPosition);
                accountRepository.save(account.get());
                accountPositionRepository.save(accountPosition);
                return "Add succesfully";
            }
        }catch (Exception e){
            return e.getMessage();
        }
    }

    public ResponseEntity<ResponseObj> getAllTraineeApplicationByTrainee(){
        try{
            Account account = authenUntil.getCurrentUSer();
            if(account == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObj(HttpStatus.UNAUTHORIZED.toString(), "Plesae login to use this method", null));
            }

            if(!account.getRole().getRoleName().toString().trim().equalsIgnoreCase(RoleEnum.TRAINEE.toString().trim())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObj(HttpStatus.FORBIDDEN.toString(), "Your account dont have permission to do this", null));
            }

//            for(TraineeApplication traineeApplication : account.getTraineeAplicationList()){
//                if(traineeApplication != null){
//                    createDefaultTraineeSubmission(traineeApplication);
//                }
//            }

            List<TraineeApplication> traineeApplicationList = account.getTraineeAplicationList();
            List<TraineeApplicationResponse> traineeApplicationResponses = new ArrayList<>();
            if(traineeApplicationList.isEmpty()){
                return  ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Trainee application list is empty", traineeApplicationList));
            }
            for(TraineeApplication traineeApplication : traineeApplicationList){
                traineeApplicationResponses.add(covertTraineeApplicationResponse(traineeApplication));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Trainee application list" , traineeApplicationResponses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage().toString(), null));
        }
    }

    private List<Document> getRequiredDocumentsForPosition(Position position) {
        if (position == null) {
            return new ArrayList<>();
        }


        List<InputDocumentMatrix> matrices = inputDocumentMatrixRepository.findByPosition(position);

        return matrices.stream()
                .map(InputDocumentMatrix::getDocument)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private TraineeApplicationResponse covertTraineeApplicationResponse(TraineeApplication traineeApplication) {

        TraineeApplicationResponse response = new TraineeApplicationResponse();

        response.setTraineeApplicationId(traineeApplication.getId());
        response.setTraineeApplicationStatus(traineeApplication.getStatusEnum());
        response.setTraineeApplicationCreateAt(traineeApplication.getCreateAt());
        response.setTraineeApplicationUpdateAt(traineeApplication.getUpdateAt());
        response.setActive(traineeApplication.isActive());

        Position position = traineeApplication.getPosition();
        if (position != null) {
            response.setPositionName(position.getPositionName());

            Department department = position.getDepartment();
            if (department != null) {
                response.setDepartmentName(department.getDepartmentName());
            } else {
                response.setDepartmentName(null);
            }
        } else {
            response.setPositionName(null);
            response.setDepartmentName(null);
        }

        return response;
    }

    public TraineeSubmissionSummaryResponse convertToSubmissionSummaryResponse(
            Trainee_Document_Submission submission) {

        if (submission == null || submission.getDocument() == null || submission.getTraineeApplication() == null || submission.getTraineeApplication().getPosition() == null) {

            throw new IllegalArgumentException("Submission, Document, or Position cannot be null.");
        }

        Document document = submission.getDocument();
        Long positionID = submission.getTraineeApplication().getPosition().getId();
        Long documentId = submission.getDocument().getId(); // (Đã khai báo, giữ lại nếu cần)

        List<Extract_Data_Response> extractDataResponseList = new ArrayList<>();
        List<DocumentRuleValueCellResponse> documentRuleValueCellResponseList = new ArrayList<>();

        if (submission.getExtractDataTraineeDocumentList() != null && !submission.getExtractDataTraineeDocumentList().isEmpty()){
            for(Extract_Data_Trainee_Document extractDataTraineeDocument : submission.getExtractDataTraineeDocumentList()){
                Extract_Data_Response extractDataResponse = new Extract_Data_Response();
                extractDataResponse.setExtract_data_id(extractDataTraineeDocument.getId());
                extractDataResponse.setExtract_data_name(extractDataTraineeDocument.getData_name());
                extractDataResponse.setExtract_Data_value(extractDataTraineeDocument.getData());
                extractDataResponseList.add(extractDataResponse);
            }
        }



        InputDocumentMatrix inputDocumentMatrix =
                inputDocumentMatrixRepository.findByDocumentIdAndPositionId(documentId, positionID)
                        .orElse(null);

        if (inputDocumentMatrix != null &&
                inputDocumentMatrix.getDocumentRuleValueList() != null &&
                !inputDocumentMatrix.getDocumentRuleValueList().isEmpty()) {

            for(DocumentRuleValue documentRuleValue : inputDocumentMatrix.getDocumentRuleValueList()){
                // Kiểm tra an toàn cho DocumentRule trước
                if(documentRuleValue.getDocumentRule() != null) {
                    DocumentRuleValueCellResponse documentRuleValueCellResponse = new DocumentRuleValueCellResponse();
                    documentRuleValueCellResponse.setDocument_rule_id(documentRuleValue.getDocumentRule().getId());
                    documentRuleValueCellResponse.setDocument_rule_name(documentRuleValue.getDocumentRule().getDocumentRuleName());
                    documentRuleValueCellResponse.setDocument_rule_value_id(documentRuleValue.getId());
                    documentRuleValueCellResponse.setValue(documentRuleValue.getRuleValue());
                    documentRuleValueCellResponseList.add(documentRuleValueCellResponse);
                }
            }
        }


        return TraineeSubmissionSummaryResponse.builder()
                .submissionId(submission.getId())
                .documentId(document.getId())
                .requiredDocumentName(document.getDocumentName())
                .submissionStatus(submission.getStatusEnum().name())
                .apply_or_not("Applied")
                .url(submission.getFilePath())
                .documentRuleValueCellResponseList(documentRuleValueCellResponseList)
                .extractDataResponseList(extractDataResponseList)
                .build();
    }
    private TraineeSubmissionSummaryResponse createSummaryPlaceholder(Document doc) {
        return TraineeSubmissionSummaryResponse.builder()
                .submissionId(null)
                .documentId(doc.getId())
                .requiredDocumentName(doc.getDocumentName())
//                .submissionStatus(StatusEnum.Pending.name())
                .apply_or_not("Not apply")
                .url(null)
                .build();
    }

    public ResponseEntity<ResponseObj> getDetailTraineeApplication(Long applicationId) {
        try {
            Account account = authenUntil.getCurrentUSer();
            if (account == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObj(HttpStatus.UNAUTHORIZED.toString(), "Please login to use this method", null));
            }


            TraineeApplication traineeApplication = traineeApplicationRepository.findById(applicationId).orElse(null);

            if(traineeApplication == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), "trainee application id does not exist", null));
            }

            if(!account.getRole().getRoleName().toString().trim().equalsIgnoreCase(RoleEnum.TRAINEE.toString().trim())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObj(HttpStatus.FORBIDDEN.toString(), "Your account dont have permission to do this", null));
            }


            TraineeApplicationDetailResponse detailResponse = convertTraineeApplicationDetail(traineeApplication, this);

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Trainee application detail", detailResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObj> getDetailTraineeApplicationByStaff(Long applicationId) {
        try {

            TraineeApplication traineeApplication = traineeApplicationRepository.findById(applicationId).orElse(null);

            if(traineeApplication == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new com.example.Automated.Application.Mangament.dto.response.ResponseObj(HttpStatus.NOT_FOUND.toString(), "trainee application id does not exist", null));
            }
            TraineeApplicationDetailResponse detailResponse = convertTraineeApplicationDetail(traineeApplication, this);

            return ResponseEntity.status(HttpStatus.OK).body(new com.example.Automated.Application.Mangament.dto.response.ResponseObj(HttpStatus.OK.toString(), "Trainee application detail", detailResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new com.example.Automated.Application.Mangament.dto.response.ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public TraineeApplicationDetailResponse convertTraineeApplicationDetail(
            TraineeApplication traineeApplication,
            TraineeApplicationServiceImplement service) {

        TraineeApplicationDetailResponse response = new TraineeApplicationDetailResponse();

        Account account = traineeApplication.getAccount();
        Position position = traineeApplication.getPosition();



        List<TraineeSubmissionSummaryResponse> finalSubmittedDocuments = new ArrayList<>();


        List<Document> requiredDocs = getRequiredDocumentsForPosition(position);


        List<Trainee_Document_Submission> currentSubmissions = traineeApplication.getTraineeDocumentSubmissionList();


        List<Trainee_Document_Submission> latestSubmissions = new ArrayList<>();
        if (currentSubmissions != null) {
            latestSubmissions = currentSubmissions.stream()
                    .filter(s -> s.getDocument() != null)
                    .collect(Collectors.groupingBy(
                            s -> s.getDocument().getId(),
                            Collectors.maxBy(Comparator.comparing(Trainee_Document_Submission::getUpdateAt)) // Lấy cái mới nhất theo UpdateAt (hoặc CreateAt nếu UpdateAt là null)
                    ))
                    .values().stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }


        for (Document requiredDoc : requiredDocs) {


            Optional<Trainee_Document_Submission> matchingSubmission = latestSubmissions.stream()
                    .filter(s -> s.getDocument().getId()== requiredDoc.getId())
                    .findFirst();

            if (matchingSubmission.isPresent()) {

                finalSubmittedDocuments.add(service.convertToSubmissionSummaryResponse(matchingSubmission.get()));
            } else {

                finalSubmittedDocuments.add(service.createSummaryPlaceholder(requiredDoc));
            }
        }



        response.setTraineeApplicationId(traineeApplication.getId());
        response.setTraineeApplicationStatus(traineeApplication.getStatusEnum());
        response.setTraineeApplicationCreateAt(traineeApplication.getCreateAt());
        response.setTraineeApplicationUpdateAt(traineeApplication.getUpdateAt());


        response.setPositionId(position != null ? position.getId() : null);
        response.setPositionName(position != null ? position.getPositionName() : null);
        response.setPositionDescription(position != null ? position.getPositionDescription() : null);
        response.setDepartmentName((position != null && position.getDepartment() != null) ? position.getDepartment().getDepartmentName() : null);


        response.setAccountId(account != null ? account.getId() : null);
        response.setFullName(account != null ? account.getUserName() : null);

        response.setSubmittedDocuments(finalSubmittedDocuments);

        return response;
    }

//    public TraineeSubmissionDetailResponse convertTraineeSubmissionDetailResponse(
//            Trainee_Document_Submission submission) {
//
//        TraineeSubmissionDetailResponse response = new TraineeSubmissionDetailResponse();
//
//
//        String requiredDocName = submission.getDocument() != null
//                ? submission.getDocument().getDocumentName()
//                : null;
//
//        String uploadTimeString = submission.getCreateAt() != null
//                ? submission.getCreateAt().toString()
//                : null;
//
//        response.setSubmissionId(submission.getId());
//        response.setRequiredDocumentName(requiredDocName);
//        response.setSubmissionStatus(submission.getStatusEnum().name());
//        response.setTakeNote(submission.getTake_note());
//
//
//        response.setFileDownloadUrl(submission.getFilePath());
//
//        response.setUploadTime(uploadTimeString);
//
//        return response;
//    }



    @Transactional
    public double calculateApplicationProgressAndSyncStatus(TraineeApplication traineeApplication) {


        Position position = traineeApplication.getPosition();
        List<Document> requiredDocs = getRequiredDocumentsForPosition(position);

        if (requiredDocs.isEmpty()) {

            return 100.0;
        }

        int totalRequired = requiredDocs.size();

           List<Trainee_Document_Submission> currentSubmissions = traineeApplication.getTraineeDocumentSubmissionList();


        long approvedCount = 0;
        long totalSubmittedAndReviewed = 0;

        if (currentSubmissions != null) {

            List<Trainee_Document_Submission> latestSubmissions = currentSubmissions.stream()
                    .filter(s -> s.getDocument() != null)
                    .collect(Collectors.groupingBy(
                            s -> s.getDocument().getId(),
                            Collectors.maxBy(Comparator.comparing(Trainee_Document_Submission::getUpdateAt))
                    ))
                    .values().stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());


            for (Document requiredDoc : requiredDocs) {
                Optional<Trainee_Document_Submission> matchingSubmission = latestSubmissions.stream()
                        .filter(s -> s.getDocument().getId() == requiredDoc.getId())
                        .findFirst();

                if (matchingSubmission.isPresent()) {
                    Trainee_Document_Submission submission = matchingSubmission.get();

                    totalSubmittedAndReviewed++;


                    if (submission.getStatusEnum() == StatusEnum.Approve) {
                        approvedCount++;
                    }
                }
            }
        }



        double progressPercentage = (double) totalSubmittedAndReviewed / totalRequired * 100.0;



        if (totalSubmittedAndReviewed == totalRequired) {


            if (approvedCount == totalRequired) {

                traineeApplication.setStatusEnum(StatusEnum.Approve);
                traineeApplicationRepository.save(traineeApplication);
            }
//            } else {
//
//                traineeApplication.setStatusEnum(StatusEnum.Reject);
//                traineeApplicationRepository.save(traineeApplication);
//            }

        }

        return progressPercentage;
    }

    @Transactional
    public ResponseEntity<ResponseObj> completeTraineeApplication(long traineeApplicationId) {
        try {

            TraineeApplication application = traineeApplicationRepository.findById(traineeApplicationId)
                    .orElse(null);

            if (application == null) {
                String msg = "Trainee Application not found with ID: " + traineeApplicationId;
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(
                        HttpStatus.NOT_FOUND.toString(), msg, null));
            }


            if (application.getStatusEnum() != StatusEnum.Approve) {
                String errorMsg = String.format(
                        "Cannot set application ID %d to Complete. Current status is '%s', required status is 'Approve'.",
                        traineeApplicationId, application.getStatusEnum().name());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(
                        HttpStatus.BAD_REQUEST.toString(), errorMsg, application));
            }


            application.setStatusEnum(StatusEnum.Complete);
            TraineeApplication updatedApplication = traineeApplicationRepository.save(application);

            String successMsg = String.format("Successfully set Trainee Application ID %d status to '%s'.",
                    traineeApplicationId, StatusEnum.Complete.name());

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                    HttpStatus.OK.toString(), successMsg, updatedApplication));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error completing Trainee Application: " + e.getMessage(), null));
        }
    }

        public ResponseEntity<ResponseObj> filterTraineeApplicationsByPosition(long positionId) {
            try {

                List<TraineeApplication> filteredApplications =
                        traineeApplicationRepository.findByPosition_Id(positionId);

                List<TraineeApplicationResponse> traineeApplicationResponses = new ArrayList<>();

                for(TraineeApplication traineeApplication : filteredApplications){
                    if(traineeApplication.getStatusEnum() == StatusEnum.Complete || traineeApplication.getStatusEnum() == StatusEnum.Approve){
                        traineeApplicationResponses.add(covertTraineeApplicationResponse(traineeApplication));
                    }
                }

                if (filteredApplications.isEmpty()) {
                    String msg = String.format("Không tìm thấy Hồ sơ ứng tuyển nào cho Vị trí ID %d.", positionId);
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                            HttpStatus.OK.toString(), msg, null));
                }

                String msg = String.format("Lấy thành công %d hồ sơ ứng tuyển cho Position ID %d.",
                        filteredApplications.size(), positionId);

                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                        HttpStatus.OK.toString(), msg, traineeApplicationResponses));

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        "Lỗi khi lọc hồ sơ ứng tuyển theo Position: " + e.getMessage(),
                        null));
            }
        }

        public ResponseEntity<ResponseObj> getTraineeApplicationByStatusForStaff(StatusEnum statusEnum){
           try{
               List<TraineeApplication> traineeApplicationList = traineeApplicationRepository.findAll().stream()
                       .filter(traineeApplication -> traineeApplication.getStatusEnum().toString() == statusEnum.toString())
                       .collect(Collectors.toList());
               if(traineeApplicationList.isEmpty()){
                   return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Trainee application list of this status " + statusEnum.toString() + " is empty", null));
               }

               List<TraineeApplicationResponse> traineeApplicationResponses = new ArrayList<>();
               for(TraineeApplication traineeApplication : traineeApplicationList){
                   traineeApplicationResponses.add(this.covertTraineeApplicationResponse(traineeApplication));
               }

               return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Trainee application List", traineeApplicationList));

           }catch (Exception e){
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
           }
        }

        public ResponseEntity<ResponseObj> getAllTraineeApplicationByStaff(){
            try{
                List<TraineeApplication> traineeApplicationList = traineeApplicationRepository.findAll().stream()
                        .filter(traineeApplication -> traineeApplication.isActive() == true)
                        .collect(Collectors.toList());
                if(traineeApplicationList.isEmpty()){
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Trainee application list is empty", null));
                }

                List<TraineeApplicationResponse> traineeApplicationResponses = new ArrayList<>();
                for(TraineeApplication traineeApplication : traineeApplicationList){
                    traineeApplicationResponses.add(this.covertTraineeApplicationResponse(traineeApplication));
                }

                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Trainee application List", traineeApplicationList));
            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
            }
        }

    public ResponseEntity<ResponseObj> getAllTraineeApplicationApproveByStaff() {
        try {
            List<TraineeApplication> approvedActiveApplications =
                    traineeApplicationRepository.findByStatusEnumAndIsActive(StatusEnum.Approve, true);

            if (approvedActiveApplications.isEmpty()) {
                String msg = "No approved and active Trainee Applications found.";
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                        HttpStatus.OK.toString(), msg, null));
            }

            List<TraineeApplicationResponse> traineeApplicationResponses = new ArrayList<>();
            for(TraineeApplication traineeApplication : approvedActiveApplications){
                traineeApplicationResponses.add(covertTraineeApplicationResponse(traineeApplication));
            }

            String msg = String.format("Successfully retrieved %d approved and active applications.",
                    approvedActiveApplications.size());

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                    HttpStatus.OK.toString(), msg, traineeApplicationResponses));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "Error retrieving approved applications: " + e.getMessage(),
                    null));
        }
    }


    public ResponseEntity<ResponseObj> uploadTraineeApplication(long trainee_application_id){
        try{
            Optional<TraineeApplication> traineeApplication = traineeApplicationRepository.findById(trainee_application_id);
            if(!traineeApplication.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), "Application does not exist", null));
            }

            if (traineeApplication.get().getStatusEnum() != StatusEnum.Pending) {
                String message = "Application ID " + trainee_application_id + " cannot be uploaded. Current status is " + traineeApplication.get().getStatusEnum().name();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), message, null));
            }
            for(Trainee_Document_Submission traineeDocumentSubmission : traineeApplication.get().getTraineeDocumentSubmissionList()){
                if(traineeDocumentSubmission == null){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Please create this trainee submission" + traineeDocumentSubmission.getDocument().getDocumentName(), null));
                }else{
                    traineeDocumentSubmission.setStatusEnum(StatusEnum.InProgress);
                }
            }
            List<Trainee_Document_Submission> traineeDocumentSubmissionList = traineeApplication.get().getTraineeDocumentSubmissionList();
            traineeApplication.get().setStatusEnum(StatusEnum.InProgress);
            traineeApplicationRepository.save(traineeApplication.get());
            traineeSubmissionRepository.saveAll(traineeDocumentSubmissionList);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Upload trainee application succesfully", null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public TraineeDashboardStatResponse getTraineeStats() {

        Account currentAccount = authenUntil.getCurrentUSer();

        TraineeApplication application = traineeApplicationRepository
                .findFirstByAccountIdOrderByCreateAtDesc(currentAccount.getId());

        if (application == null) {

            return TraineeDashboardStatResponse.builder()
                    .progressPercentage(0).totalSubmissions(0)
                    .approvedCount(0).rejectedCount(0).inProgressCount(0)
                    .build();
        }

        Long applicationId = application.getId();

        long total = traineeSubmissionRepository.countByTraineeApplication_Id(applicationId);
        long approved = traineeSubmissionRepository.countByTraineeApplication_IdAndStatusEnum(applicationId, StatusEnum.Approve);
        long rejected = traineeSubmissionRepository.countByTraineeApplication_IdAndStatusEnum(applicationId, StatusEnum.Reject);
        long inProgress = traineeSubmissionRepository.countByTraineeApplication_IdAndStatusEnum(applicationId, StatusEnum.InProgress);

        double percentage = 0;
        if (total > 0) {

            percentage = ((double) approved / total) * 100;
        }

        return TraineeDashboardStatResponse.builder()
                .progressPercentage(Math.round(percentage * 100.0) / 100.0)
                .totalSubmissions(total)
                .approvedCount(approved)
                .rejectedCount(rejected)
                .inProgressCount(inProgress)
                .build();
    }


    public StaffDashboardResponse getStaffDashboard() {

        Object[] results = (Object[]) traineeApplicationRepository.getStaffGlobalStats();

        long total = (long) results[0];
        long inProgress = (long) results[1];
        long reject = (long) results[2];
        long approve = (long) results[3];
        long complete = (long) results[4];

        double completionRate = 0;
        if (approve > 0) {
            completionRate = ((double) complete / approve) * 100;
        }

        return StaffDashboardResponse.builder()
                .totalApplications(total)
                .inProgressCount(inProgress)
                .rejectCount(reject)
                .approveCount(approve)
                .completeCount(complete)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .build();
    }
}