package com.example.Automated.Application.Mangament.dto.request;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleCellUpdateDTO {
    private Long matrixId;
    private List<DocumentRuleValueUpdateDTO> documentRuleValueUpdateDTOList;
}
