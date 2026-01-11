package com.example.Automated.Application.Mangament.dto.response;

import lombok.*;

import java.util.List;
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentWithRulesResponse {
    private Long documentId;
    private String documentName;
    private String documentDescription;
    private List<DocumentRuleSlimResponse> documentRules;

}
