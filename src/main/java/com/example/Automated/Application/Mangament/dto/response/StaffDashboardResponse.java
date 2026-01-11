package com.example.Automated.Application.Mangament.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffDashboardResponse {
    private long totalApplications;     // Tổng số hồ sơ trong hệ thống
    private long inProgressCount;        // Đang chờ xử lý
    private long rejectCount;            // Đã bị từ chối
    private long approveCount;           // Đã được duyệt (nhưng chưa hoàn tất)
    private long completeCount;          // Đã hoàn thành hồ sơ
    private double completionRate;
}
