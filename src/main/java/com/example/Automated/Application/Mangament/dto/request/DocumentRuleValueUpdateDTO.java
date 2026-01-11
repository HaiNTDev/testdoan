package com.example.Automated.Application.Mangament.dto.request;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRuleValueUpdateDTO {
    private Long document_rule_value_id;
    private String rule_value;
}
