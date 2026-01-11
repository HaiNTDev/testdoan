package com.example.Automated.Application.Mangament.dto.response;

import com.example.Automated.Application.Mangament.enums.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeApplicationResponse {
    private long traineeApplicationId;
    private StatusEnum traineeApplicationStatus;
    private String positionName;
    private String departmentName;
    private LocalDateTime traineeApplicationCreateAt;
    private LocalDateTime traineeApplicationUpdateAt;
    private boolean isActive;
}
