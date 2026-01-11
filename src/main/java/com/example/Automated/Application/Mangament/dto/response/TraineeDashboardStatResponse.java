package com.example.Automated.Application.Mangament.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TraineeDashboardStatResponse {
    private double progressPercentage;
    private long totalSubmissions;
    private long approvedCount;
    private long rejectedCount;
    private long inProgressCount;
}
