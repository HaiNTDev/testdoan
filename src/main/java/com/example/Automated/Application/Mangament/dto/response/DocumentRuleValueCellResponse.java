package com.example.Automated.Application.Mangament.dto.response;


import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentRuleValueCellResponse {
    private Long document_rule_value_id;
    private String value;
    private Long document_rule_id;
    private String document_rule_name;
}
