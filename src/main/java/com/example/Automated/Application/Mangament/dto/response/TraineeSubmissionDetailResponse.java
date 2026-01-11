package com.example.Automated.Application.Mangament.dto.response;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeSubmissionDetailResponse  {
    private Long submissionId;
    private long document_id;
    private String requiredDocumentName;
    private String submissionStatus;
    private String report;
    private String submission_name;
    private String takeNote;
    private List<String> fileDownloadUrl;
    private LocalDateTime uploadTime;

}
