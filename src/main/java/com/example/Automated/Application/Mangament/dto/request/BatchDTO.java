package com.example.Automated.Application.Mangament.dto.request;


import com.google.api.client.util.DateTime;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
