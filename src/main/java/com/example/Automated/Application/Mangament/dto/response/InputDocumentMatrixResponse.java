package com.example.Automated.Application.Mangament.dto.response;

import com.example.Automated.Application.Mangament.enums.MatrixStatusEnum;
import com.example.Automated.Application.Mangament.enums.StatusEnum;
import lombok.*;

import javax.swing.plaf.nimbus.State;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputDocumentMatrixResponse {
    private Long positionId;
    private String positionName;
    private StatusEnum statusEnum;
    private MatrixStatusEnum matrixStatusEnum;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reject_reason;
    private Long departmentId;

    private List<DocumentCollumResponse> documentCollumResponseList;
}
