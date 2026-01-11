package com.example.Automated.Application.Mangament.dto.response;


import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.example.Automated.Application.Mangament.model.DocumentRuleValue;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentCollumResponse {
    private Long document_id;
    private String document_name;
    private StatusEnum statusEnum;
    private boolean isRequired;
    private Long matrixId;

    private List<DocumentRuleValueCellResponse> documentRuleValueList;
}
