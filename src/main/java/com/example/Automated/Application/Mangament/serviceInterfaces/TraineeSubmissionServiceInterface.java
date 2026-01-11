package com.example.Automated.Application.Mangament.serviceInterfaces;


import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public interface TraineeSubmissionServiceInterface {

    public ResponseEntity<ResponseObj> createTraineeSubmission(long documentID, long trainee_application_id, String submission_name, String take_note, List<MultipartFile> submission_document_file);

    public ResponseEntity<ResponseObj> updateTraineeSubmission(
            long submissionID,
            String new_submission_name,
            String new_take_note,
            List<MultipartFile> new_submission_document_files);

    public ResponseEntity<ResponseObj> getTraineeSubmissionDetail(long traineeSubmissionId);
}
