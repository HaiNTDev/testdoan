package com.example.Automated.Application.Mangament.dto.request;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleEvaluation {
    private String rule_name;
    private String status;

    private String reason;
}
