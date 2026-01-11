package com.example.Automated.Application.Mangament.dto.response;

import com.google.api.client.util.DateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BatchResponse {
   private LocalDateTime startDate;
   private  LocalDateTime endDate;
   private  boolean status;
}
