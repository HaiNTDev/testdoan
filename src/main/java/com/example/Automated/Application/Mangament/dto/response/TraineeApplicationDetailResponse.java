package com.example.Automated.Application.Mangament.dto.response;

import com.example.Automated.Application.Mangament.enums.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeApplicationDetailResponse {
    private Long traineeApplicationId;
    private StatusEnum traineeApplicationStatus;
    private LocalDateTime traineeApplicationCreateAt;
    private LocalDateTime traineeApplicationUpdateAt;


    private Long positionId;
    private String positionName;
    private String departmentName;
    private String positionDescription;


    private Long accountId;
    private String fullName;


//    private List<Extract_Data_Response> extractDataResponseList;
//    private List<DocumentRuleValueCellResponse> documentRuleValueCellResponses;

    private List<TraineeSubmissionSummaryResponse> submittedDocuments;
}
