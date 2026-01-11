package com.example.Automated.Application.Mangament.serviceInterfaces;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.dto.response.StaffDashboardResponse;
import com.example.Automated.Application.Mangament.dto.response.TraineeDashboardStatResponse;
import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.example.Automated.Application.Mangament.model.Account;
import com.example.Automated.Application.Mangament.model.TraineeApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public interface TraineeApplicationServiceInterface {
    public void createDefaultApplication(Account account);

    double calculateApplicationProgressAndSyncStatus(TraineeApplication traineeApplication);

     ResponseEntity<ResponseObj> completeTraineeApplication(long traineeApplicationId);

    ResponseEntity<ResponseObj> getAllTraineeApplicationApproveByStaff();

    ResponseEntity<ResponseObj> filterTraineeApplicationsByPosition(long positionId);

    ResponseEntity<ResponseObj> getAllTraineeApplicationByTrainee();

    ResponseEntity<ResponseObj> getDetailTraineeApplication(Long applicationId);

    ResponseEntity<ResponseObj> getDetailTraineeApplicationByStaff(Long applicationId);

    ResponseEntity<ResponseObj> uploadTraineeApplication(long trainee_application_id);

    ResponseEntity<ResponseObj> getTraineeApplicationByStatusForStaff(StatusEnum statusEnum);

    ResponseEntity<ResponseObj> getAllTraineeApplicationByStaff();

    public TraineeDashboardStatResponse getTraineeStats();

    public StaffDashboardResponse getStaffDashboard();
}
