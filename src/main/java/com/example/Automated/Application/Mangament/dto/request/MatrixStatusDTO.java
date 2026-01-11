package com.example.Automated.Application.Mangament.dto.request;

import com.example.Automated.Application.Mangament.enums.StatusEnum;
import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatrixStatusDTO {
    private StatusEnum status;
}
