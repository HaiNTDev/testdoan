package com.example.Automated.Application.Mangament.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeSubmissionSummaryResponse {
    private Long submissionId;

    private Long documentId;
    private String requiredDocumentName;

    private String apply_or_not;
    private String submissionStatus;
    private String url;

    private List<DocumentRuleValueCellResponse> documentRuleValueCellResponseList;
    private List<Extract_Data_Response> extractDataResponseList;
}
