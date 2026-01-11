package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.model.Extract_Data_Trainee_Document;
import com.example.Automated.Application.Mangament.model.Trainee_Document_Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface Extract_Data_Trainee_Document_Repository extends JpaRepository<Extract_Data_Trainee_Document, Long> {

    long deleteByTraineeDocumentSubmission(Trainee_Document_Submission traineeDocumentSubmission);

    List<Extract_Data_Trainee_Document> findByTraineeDocumentSubmission(Trainee_Document_Submission traineeDocumentSubmission);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM extract_data_trainee_document WHERE trainee_submission_id = :subId", nativeQuery = true)
    void deleteBySubmissionIdNative(@Param("subId") Long subId);

    @Query(value = "SELECT * FROM extract_data_trainee_document WHERE trainee_submission_id = :subId", nativeQuery = true)
    List<Extract_Data_Trainee_Document> findDataNative(@Param("subId") Long subId);
}
