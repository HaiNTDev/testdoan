package com.example.Automated.Application.Mangament.dto.request;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeMatrixDTO {
    private LocalDateTime startDate_deadLine;
    private LocalDateTime endDate_deadLine;
}
