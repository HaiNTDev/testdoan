package com.example.Automated.Application.Mangament.dto.request;


import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleCellCreationDTO {
    private Long matrixID;
    private List<DocumentRuleValueDTO> documentRuleValueDTOList;
}
