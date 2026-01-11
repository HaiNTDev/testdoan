package com.example.Automated.Application.Mangament.dto.request;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatrixExpirationDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
