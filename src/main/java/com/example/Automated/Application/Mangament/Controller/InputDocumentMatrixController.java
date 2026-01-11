package com.example.Automated.Application.Mangament.Controller;

import com.example.Automated.Application.Mangament.dto.request.*;
import com.example.Automated.Application.Mangament.dto.response.InputDocumentMatrixResponse;
import com.example.Automated.Application.Mangament.dto.response.MatrixDashBoardResponse;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.example.Automated.Application.Mangament.serviceInterfaces.InputDocumentMatrixServiceInterface;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matrix")
public class InputDocumentMatrixController {
    @Autowired
    private InputDocumentMatrixServiceInterface inputDocumentMatrixServiceInterface;

    @GetMapping("/department/{departmentID}")
    public ResponseEntity<ResponseObj> getMatrixByDepartmentId(@PathVariable Long departmentID) {
        return inputDocumentMatrixServiceInterface.getMatrixResponseByDepartmentId(departmentID);
    }

    @GetMapping("get_matrix_filter_by_position_department")
    public ResponseEntity<ResponseObj> filterMatrixByDepartmentPosition(@RequestParam(required = false) Long departmentId, @RequestParam(required = false) Long positionId){
        return inputDocumentMatrixServiceInterface.filterMatrixByDeptAndPos(departmentId, positionId);
    }

    @GetMapping("/getAllMatrix")
    public ResponseEntity<ResponseObj> getAllMatrix() {
        return inputDocumentMatrixServiceInterface.getAllMatrix();
    }

    @GetMapping("/matrix_details")
    public ResponseEntity<ResponseObj> getDetails(@RequestParam List<Long> matrixIds) {
        if (matrixIds == null || matrixIds.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseObj("400", "Matrix ID list cannot be empty", null));
        }

        List<InputDocumentMatrixResponse> data = inputDocumentMatrixServiceInterface.getMatrixDetails(matrixIds);
        return ResponseEntity.ok(new ResponseObj("200", "Fetch matrix details successfully", data));
    }

    @GetMapping("input_matrix_document_dashboard")
    public  MatrixDashBoardResponse matrixDashboard(){
        return inputDocumentMatrixServiceInterface.getMatrixApprovalProgressByDepartment();
    }

    @PostMapping("/addRow_for_training_director")
    public ResponseEntity<ResponseObj> addSingleRow(@RequestBody PositionRequestDTO positionRequestDTO) {
        return inputDocumentMatrixServiceInterface.addRow(positionRequestDTO);
    }


    @PostMapping("/addMultipleRow_for_training_director")
    public ResponseEntity<ResponseObj> addMultipleRows(@RequestBody List<PositionRequestDTO> positionRequestDTOList) {
        return inputDocumentMatrixServiceInterface.addAllRows(positionRequestDTOList);
    }

    @PutMapping(value = "/set-status/department/{departmentId}_for_training_director_approve_or_reject", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObj> setMatrixStatusByDepartment(
            @PathVariable Long departmentId,
            @RequestParam StatusEnum statusEnum,
            @RequestPart String rejectReason){
        return inputDocumentMatrixServiceInterface.setApproveOrRejectStatus(departmentId, statusEnum, rejectReason);
    }

    @PutMapping("/set-drafted/{departmentID}_for_head_department")
    public ResponseEntity<ResponseObj> setDraftedStatus(@PathVariable Long departmentID) {
        return inputDocumentMatrixServiceInterface.setDraftedStatus(departmentID);
    }


    @PutMapping("/setCompleteStatusToActive_for_training_director")
    public ResponseEntity<ResponseObj> setInProgressStatusToActive(@RequestBody MatrixExpirationDTO matrixExpirationDTO){
        return inputDocumentMatrixServiceInterface.setCompleteToActive(matrixExpirationDTO);
    }

    @PostMapping("/addColum_for_training_director")
    public ResponseEntity<ResponseObj> addSingleColumn(@RequestBody DocumentRequestDTO documentRequestDTO) {
        return inputDocumentMatrixServiceInterface.addColumn(documentRequestDTO);
    }


    @PostMapping("/addMultipleColum_for_training_director")
    public ResponseEntity<ResponseObj> addMultipleColumns(@RequestBody List<DocumentRequestDTO> documentRequestDTOList) {
        return inputDocumentMatrixServiceInterface.addColumns(documentRequestDTOList);
    }
    @DeleteMapping("/deleteRow_for_training_director/{positionId}")
    public ResponseEntity<ResponseObj> deleteRow(@PathVariable Long positionId) {
        return inputDocumentMatrixServiceInterface.deleteRow(positionId);
    }

    @DeleteMapping("/deleteColumn_for_training_director/{documentId}")
    public ResponseEntity<ResponseObj> deleteColumn(@PathVariable Long documentId) {
        return inputDocumentMatrixServiceInterface.deleteColumn(documentId);
    }

    @DeleteMapping("/deleteAllRow_for_training_director")
    public ResponseEntity<ResponseObj> deleteAllRow(){
        return inputDocumentMatrixServiceInterface.deleteAllRow();
    }

    @DeleteMapping("/deleteAllColumns_for_training_director")
    public ResponseEntity<ResponseObj> deleteAllMatrixColumns() {

        return inputDocumentMatrixServiceInterface.deleteAllMatrixColumns();
    }

    @DeleteMapping("/clearMatrix_for_training_director")
    public ResponseEntity<ResponseObj> clearAllMatrixData() {
          return inputDocumentMatrixServiceInterface.clearAllMatrixData();
    }

    @PostMapping("/setPendintStatusMatrix_for_training_director")
    public  ResponseEntity<ResponseObj> setPendinStauts(@RequestBody TimeMatrixDTO timeMatrixDTO){
        return inputDocumentMatrixServiceInterface.setPendingStatusMatrix(timeMatrixDTO);
    }

//    @PostMapping("/clickToSell")  
//    public  ResponseEntity<ResponseObj> clickToSell(@RequestBody List<Long> matrixId){
//        return inputDocumentMatrixServiceInterface.clickToCell(matrixId);
//    }

    @PostMapping("/clickToCellMatrix_for_head_of_department")
    public ResponseEntity<ResponseObj> clicktoCell(@RequestBody CellMatrixDTO cellMatrixDTO){
        return inputDocumentMatrixServiceInterface.clickToSellRequired(cellMatrixDTO);
    }
}