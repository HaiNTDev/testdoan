package com.example.Automated.Application.Mangament.Controller;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.serviceInterfaces.TraineeSubmissionServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/trainee_submission")
public class TraineeSubmissionController {
    @Autowired
    private TraineeSubmissionServiceInterface traineeSubmissionServiceInterface;

    @GetMapping("/get_trainee_submission_detail/{trainee_submission_id}")
    public ResponseEntity<ResponseObj> getTraineeSubmissionDetail(@PathVariable Long trainee_submission_id){
        return traineeSubmissionServiceInterface.getTraineeSubmissionDetail(trainee_submission_id);
    }

    @PostMapping(value = "/create_trainee_submission_by_trainee", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObj> createTraineeSubmissionByTrainee(
            @RequestPart(value = "documentID", required = false) String documentID,
            @RequestPart(value = "traineeApplicationId", required = false) String trainee_application_id,
            @RequestPart(value = "requiredDocumentName", required = false) String submission_name,
            @RequestPart(value = "takeNote", required = false) String take_note,
            @RequestPart(value = "submissionDocumentFile",required = false) List<MultipartFile> submission_document_file) {


        long document_id;
        long trainee_applicationId;


        try {
            document_id = Long.parseLong(documentID);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "document_id should be digit", null));
        }


        try {
            trainee_applicationId = Long.parseLong(trainee_application_id);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Trainee_application_id should be digit", null));
        }


        return traineeSubmissionServiceInterface.createTraineeSubmission(
                document_id,
                trainee_applicationId,
                submission_name,
                take_note,
                submission_document_file
        );
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObj> updateSubmission(
            @RequestPart(value = "submissionID", required = false) String submissionID,
            @RequestPart(value = "requiredDocumentName", required = false) String new_submission_name,
            @RequestPart(value = "newTakeNote", required = false) String new_take_note,
            @RequestPart(value = "newSubmissionDocumentFile", required = false) List<MultipartFile> new_submission_document_file) {
        try {
            long submissionId = Long.parseLong(submissionID);
            return traineeSubmissionServiceInterface.updateTraineeSubmission(
                    submissionId,
                    new_submission_name,
                    new_take_note,
                    new_submission_document_file
            );
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));

        }

    }
}
