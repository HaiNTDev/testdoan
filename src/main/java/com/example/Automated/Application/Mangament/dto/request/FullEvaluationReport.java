package com.example.Automated.Application.Mangament.dto.request;

import lombok.*;

import java.util.List;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FullEvaluationReport {
    private String OVERALL_STATUS;
    private String FAILED_COUNT;
    private List<RuleEvaluation> EVALUATED_RULES;
}
