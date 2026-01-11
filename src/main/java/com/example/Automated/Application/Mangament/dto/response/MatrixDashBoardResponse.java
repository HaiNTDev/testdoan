package com.example.Automated.Application.Mangament.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatrixDashBoardResponse {
    private long approvedCount;
    private long rejectedCount;
    private long draftedCount;
    private long InProgressCount;
    private double approvalProgressPercentage;


}