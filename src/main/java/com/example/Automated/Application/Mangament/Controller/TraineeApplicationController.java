package com.example.Automated.Application.Mangament.Controller;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.dto.response.StaffDashboardResponse;
import com.example.Automated.Application.Mangament.dto.response.TraineeDashboardStatResponse;
import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.example.Automated.Application.Mangament.serviceInterfaces.TraineeApplicationServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainee_application")
public class TraineeApplicationController {
    @Autowired
    private TraineeApplicationServiceInterface traineeApplicationServiceInterface;

    @GetMapping("/get_all_application_by_trainee")
    public ResponseEntity<ResponseObj> getAllTraineeApplicationByTrainee(){
        return traineeApplicationServiceInterface.getAllTraineeApplicationByTrainee();
    }

    @GetMapping("/get_trainee_application_detail_by_trainee/{trainee_application_id}")
    public ResponseEntity<ResponseObj> getTraineeApplicationDetailByTrainee(@PathVariable Long trainee_application_id){
        return traineeApplicationServiceInterface.getDetailTraineeApplication(trainee_application_id);
    }
//
//    @GetMapping("/filter-trainee-application-by-position-by-staff-academic")
//    public ResponseEntity<ResponseObj> filterTraineeApplicationsByPosition(
//            @RequestParam long positionId) {
//
//        return traineeApplicationServiceInterface.filterTraineeApplicationsByPosition(positionId);
//    }

    @GetMapping("/get_all_trainee_application_by_staff_academic_affair")
    public ResponseEntity<ResponseObj> getAllTraineeApplicationByStaffAffair(){
        return traineeApplicationServiceInterface.getAllTraineeApplicationByStaff();
    }

    @GetMapping("/get_trainee_application_detail_by_staff/{trainee_application_id}")
    public ResponseEntity<ResponseObj> getTraineeApplicationDetailByStaff(@PathVariable Long trainee_application_id){
        return traineeApplicationServiceInterface.getDetailTraineeApplicationByStaff(trainee_application_id);
    }

    @GetMapping("/get_trainee_application_list_by_status_by_staff_academic_staff_affair")
    public ResponseEntity<ResponseObj> getTraineeApplicationByStatusByStaff(@RequestParam StatusEnum statusEnum){
        return traineeApplicationServiceInterface.getTraineeApplicationByStatusForStaff(statusEnum);
    }

//
//    @GetMapping("/getAllTraineeApplicationByStaffAcademic")
//    public ResponseEntity<ResponseObj> getAllTraineeApplicationApproveByStaff() {
//        return traineeApplicationServiceInterface.getAllTraineeApplicationApproveByStaff();
//    }

    @PutMapping("/{traineeApplicationId}/complete")
    public ResponseEntity<ResponseObj> completeTraineeApplication(
            @PathVariable long traineeApplicationId) {

        return traineeApplicationServiceInterface.completeTraineeApplication(traineeApplicationId);
    }

    @GetMapping("/TraineeApplicationDashboardByTrainee")
    public ResponseEntity<TraineeDashboardStatResponse> getMyStats() {

        TraineeDashboardStatResponse stats = traineeApplicationServiceInterface.getTraineeStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/overall-stats_trainee_application_for_staff_academic_affair")
//    @PreAuthorize("hasRole('STAFF') or hasRole('ACADEMIC_AFFAIR')")
    public ResponseEntity<StaffDashboardResponse> getOverallStats() {
        return ResponseEntity.ok(traineeApplicationServiceInterface.getStaffDashboard());
    }
//
//    @PostMapping("/upload_trainee_application/{trainee_application_id}")
//    public ResponseEntity<ResponseObj> uploadTraineeApplication(@PathVariable Long trainee_application_id){
//        return traineeApplicationServiceInterface.uploadTraineeApplication(trainee_application_id);
//    }
}
