package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.example.Automated.Application.Mangament.model.TraineeApplication;
import com.example.Automated.Application.Mangament.model.Trainee_Document_Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraineeSubmissionRepository extends JpaRepository<Trainee_Document_Submission, Long> {
    boolean existsByDocumentIdAndTraineeApplicationIdAndStatusEnumIn(long documentId, long traineeApplicationId, List<StatusEnum> blockingStatuses);

    List<Trainee_Document_Submission> findByTraineeApplication(TraineeApplication traineeApplication);


    long countByTraineeApplication_Id(Long applicationId);

    long countByTraineeApplication_IdAndStatusEnum(Long applicationId, StatusEnum status);
}
