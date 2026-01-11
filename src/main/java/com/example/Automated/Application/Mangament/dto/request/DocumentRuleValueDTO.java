package com.example.Automated.Application.Mangament.dto.request;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRuleValueDTO {
    private Long document_rule_Id;
    private String document_rule_value;
}
