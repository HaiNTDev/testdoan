package com.example.Automated.Application.Mangament.serviceInterfaces;


import com.example.Automated.Application.Mangament.dto.request.*;
import com.example.Automated.Application.Mangament.dto.response.InputDocumentMatrixResponse;
import com.example.Automated.Application.Mangament.dto.response.MatrixDashBoardResponse;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.enums.StatusEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface InputDocumentMatrixServiceInterface {
    ResponseEntity<ResponseObj> getMatrixResponseByDepartmentId(Long departmentID);
    ResponseEntity<ResponseObj> addAllRows(List<PositionRequestDTO> positionRequestDTOList);
    ResponseEntity<ResponseObj> addRow(PositionRequestDTO positionRequestDTO);
    ResponseEntity<ResponseObj> getAllMatrix();
    ResponseEntity<ResponseObj> addColumn(DocumentRequestDTO documentRequestDTO);
    ResponseEntity<ResponseObj> addColumns(List<DocumentRequestDTO> documentRequestDTOList);

    ResponseEntity<ResponseObj> deleteAllRow();
    ResponseEntity<ResponseObj> deleteRow(Long positionId);
    ResponseEntity<ResponseObj> deleteColumn(Long documentId);

    ResponseEntity<ResponseObj> clearAllMatrixData();
    ResponseEntity<ResponseObj> deleteAllMatrixColumns();
    ResponseEntity<ResponseObj> deleteAllMatrixRowsAndColumns();
    ResponseEntity<ResponseObj> setPendingStatusMatrix(TimeMatrixDTO timeMatrixDTO);
    ResponseEntity<ResponseObj> clickToCell(List<Long> matrixId);
    ResponseEntity<ResponseObj> clickToSellRequired(CellMatrixDTO cellMatrixDTO);
    ResponseEntity<ResponseObj> setApproveOrRejectStatus(Long departmentId, StatusEnum statusEnum,String reject_reason);
    ResponseEntity<ResponseObj> setCompleteToActive(MatrixExpirationDTO matrixExpirationDTO);

    public MatrixDashBoardResponse getMatrixApprovalProgressByDepartment();

    ResponseEntity<ResponseObj> setDraftedStatus(Long departmentID);

    public ResponseEntity<ResponseObj> filterMatrixByDeptAndPos(Long departmentId, Long positionId);

    public List<InputDocumentMatrixResponse> getMatrixDetails(List<Long> matrixIds);
}
