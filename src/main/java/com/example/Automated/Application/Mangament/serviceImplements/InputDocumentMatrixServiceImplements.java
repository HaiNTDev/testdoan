package com.example.Automated.Application.Mangament.serviceImplements;


import com.example.Automated.Application.Mangament.dto.request.*;
import com.example.Automated.Application.Mangament.dto.response.*;
import com.example.Automated.Application.Mangament.enums.MatrixStatusEnum;
import com.example.Automated.Application.Mangament.enums.RoleEnum;
import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.example.Automated.Application.Mangament.model.*;
import com.example.Automated.Application.Mangament.repositories.*;
import com.example.Automated.Application.Mangament.serviceInterfaces.InputDocumentMatrixServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InputDocumentMatrixServiceImplements implements InputDocumentMatrixServiceInterface {
    @Autowired
    private InputDocumentMatrixRepository inputDocumentMatrixRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private DocumentRuleValueRepository documentRuleValueRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private NotificationService notificationService;

    public ResponseEntity<ResponseObj> getMatrixResponseByDepartmentId(Long departmentID){
        try{
            Optional<Department> departmentOpt = departmentRepository.findById(departmentID);
            if(!departmentOpt.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(),"Department does not exist", null));
            }


            ResponseEntity<ResponseObj> allMatrixResponse = getAllMatrix();
            if (allMatrixResponse.getStatusCode() != HttpStatus.OK) {
                return allMatrixResponse;
            }

            List<InputDocumentMatrixResponse> allMatrixData = (List<InputDocumentMatrixResponse>) allMatrixResponse.getBody().getData();
            List<InputDocumentMatrixResponse> departmentMatrixResponses = new ArrayList<>();


            Set<Long> positionIdsInDepartment = departmentOpt.get().getPositionList().stream()
                    .map(Position::getId)
                    .collect(Collectors.toSet());

            for (InputDocumentMatrixResponse row : allMatrixData) {
                if (positionIdsInDepartment.contains(row.getPositionId())) {
                    departmentMatrixResponses.add(row);
                }
            }

            return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "Success", departmentMatrixResponses));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(),null));
        }
    }


    public ResponseEntity<ResponseObj> addAllRows(List<PositionRequestDTO> positionRequestDTOList){
        try{
            List<Long> requestedIds = positionRequestDTOList.stream()
                    .map(PositionRequestDTO::getPositionId)
                    .collect(Collectors.toList());


            Map<String, List<Long>> categorizedIds = checkAndCategorizePositions(requestedIds);
            List<Long> positionIdsExist = categorizedIds.get("exists");
            List<Long> positionIdsToAdd = categorizedIds.get("new");


            if (positionIdsToAdd.isEmpty()) {
                String message = "All positions already exist in the matrix. Ignored IDs: " + positionIdsExist;

                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObj(HttpStatus.CONFLICT.toString(), message, null));
            }

            List<Position> positionsToSave = positionRepository.findAllById(positionIdsToAdd);
            List<Document> existingDocuments = inputDocumentMatrixRepository.findAll().stream()
                    .filter(matrix -> matrix.getDocument() != null)
                    .map(InputDocumentMatrix::getDocument)
                    .distinct()
                    .collect(Collectors.toList());

            List<InputDocumentMatrix> newlyCreatedEntries = new ArrayList<>();

            List<InputDocumentMatrix> entriesToUpdate = new ArrayList<>();


            List<InputDocumentMatrix> nullPositionEntries = inputDocumentMatrixRepository.findAll().stream()
                    .filter(matrix -> matrix.getPosition() == null)
                    .collect(Collectors.toList());


           for(Position position : positionsToSave){
               if (!existingDocuments.isEmpty()) {
                   for (Document document : existingDocuments) {

                       Optional<InputDocumentMatrix> reusedEntryOpt = nullPositionEntries.stream()
                               .filter(matrix -> matrix.getDocument() != null && matrix.getDocument().getId() == (document.getId()))
                               .findFirst();

                       if (reusedEntryOpt.isPresent()) {

                           InputDocumentMatrix matrixToUpdate = reusedEntryOpt.get();
                           matrixToUpdate.setPosition(position); // Gán Position mới
                           matrixToUpdate.setUpdateAt(LocalDateTime.now());
                           matrixToUpdate.setStatusEnum(StatusEnum.InProgress);
                           entriesToUpdate.add(matrixToUpdate);


                           nullPositionEntries.remove(matrixToUpdate);

                       } else {
                           InputDocumentMatrix matrix = new InputDocumentMatrix();
                           matrix.setPosition(position);
                           matrix.setDocument(document);
                           matrix.setStatusEnum(StatusEnum.InProgress);
                           matrix.setCreateAt(LocalDateTime.now());
                           newlyCreatedEntries.add(matrix);
                       }
                   }
               } else {
                   InputDocumentMatrix matrix = new InputDocumentMatrix();
                   matrix.setPosition(position);
                   matrix.setCreateAt(LocalDateTime.now());
                   matrix.setStatusEnum(StatusEnum.InProgress);
                   newlyCreatedEntries.add(matrix);
               }

           }
            inputDocumentMatrixRepository.saveAll(entriesToUpdate);
            inputDocumentMatrixRepository.saveAll(newlyCreatedEntries);


//            for (Position position : positionsToSave) {
//
//
//                if (!existingDocuments.isEmpty()) {
//                    for (Document document : existingDocuments) {
//                        InputDocumentMatrix matrix = new InputDocumentMatrix();
//                        matrix.setPosition(position);
//                        matrix.setDocument(document);
//                        matrix.setCreateAt(LocalDateTime.now());
//                        newlyCreatedEntries.add(matrix);
//                    }
//                } else {
//
//                    InputDocumentMatrix matrix = new InputDocumentMatrix();
//                    matrix.setPosition(position);
//                    matrix.setCreateAt(LocalDateTime.now());
//                    newlyCreatedEntries.add(matrix);
//                }
//            }
//
//
//            inputDocumentMatrixRepository.saveAll(newlyCreatedEntries);



            ResponseEntity<ResponseObj> matrixResponse = getAllMatrix();
            if (matrixResponse.getStatusCode() != HttpStatus.OK) {
                return matrixResponse;
            }

            String successMsg = "Successfully added " + positionIdsToAdd.size() + " new rows and created " + newlyCreatedEntries.size() + " cells.";
            if (!positionIdsExist.isEmpty()) {
                successMsg += " (Ignored existing IDs: " + positionIdsExist + ")";
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObj(HttpStatus.CREATED.toString(), successMsg, matrixResponse.getBody().getData())
            );

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }


    public Map<String, List<Long>> checkAndCategorizePositions(List<Long> positionListId) {


        List<Long> existingPositionIdsInMatrix = inputDocumentMatrixRepository.findAll().stream()
                .filter(matrix -> matrix.getPosition() !=null)
                .map(matrix -> matrix.getPosition().getId())
                .distinct()
                .collect(Collectors.toList());

        List<Long> positionIdsExist = new ArrayList<>();
        List<Long> positionIdsNew = new ArrayList<>();

        for (Long requestedId : positionListId) {
            if (existingPositionIdsInMatrix.contains(requestedId)) {
                positionIdsExist.add(requestedId);
            } else {
                positionIdsNew.add(requestedId);
            }
        }

        Map<String, List<Long>> result = new HashMap<>();
        result.put("exists", positionIdsExist);
        result.put("new", positionIdsNew);

        return result;
    }


    private InputDocumentMatrixResponse convertToMatrixRowResponse(Position position, List<InputDocumentMatrix> assignments){
        InputDocumentMatrixResponse response = new InputDocumentMatrixResponse();

        response.setPositionId(position.getId());
        response.setPositionName(position.getPositionName());
        response.setDepartmentId(position.getDepartment().getId());

        assignments.stream()
                .findAny()
                .ifPresent(entry -> {

                    response.setStatusEnum(entry.getStatusEnum());
                    response.setMatrixStatusEnum(entry.getMatrixStatusEnum());
                    response.setStartDate(entry.getStartDate_deadLine());
                    response.setEndDate(entry.getEndDate_deadLine());
                    response.setReject_reason(entry.getRejection_reason());
                });

        List<DocumentCollumResponse> columnResponses = assignments.stream()
                .map(this::convertDocumentResponse)
                .collect(Collectors.toList());

        response.setDocumentCollumResponseList(columnResponses);
        return response;
    }

    private DocumentResponse convertDocumentToDocumentResponse(Document document) {
        if (document == null) return null;

        DocumentResponse response = new DocumentResponse();

        response.setId(document.getId());

        response.setDocumentName(document.getDocumentName());


        return response;
    }


    private DocumentCollumResponse convertDocumentResponse(InputDocumentMatrix matrix){
        DocumentCollumResponse response = new DocumentCollumResponse();

        response.setMatrixId(matrix.getId());
        response.setStatusEnum(matrix.getStatusEnum());
        response.setRequired(matrix.isRequired());


        if (matrix.getDocument() != null) {
            response.setDocument_id(matrix.getDocument().getId());
            response.setDocument_name(matrix.getDocument().getDocumentName());
        } else {
            response.setDocument_id(null);
            response.setDocument_name(null);
        }
        if (matrix.getDocumentRuleValueList() != null) {
            List<DocumentRuleValueCellResponse> ruleResponses = matrix.getDocumentRuleValueList().stream()
                    .map(this::convertDocumentRuleValue)
                    .collect(Collectors.toList());


        }

        return response;
    }

    private DocumentRuleValueCellResponse convertDocumentRuleValue(DocumentRuleValue documentRuleValue){
        DocumentRuleValueCellResponse documentRuleValueCellResponse = new DocumentRuleValueCellResponse();
        documentRuleValueCellResponse.setDocument_rule_value_id(documentRuleValue.getId());
        documentRuleValueCellResponse.setDocument_rule_name(documentRuleValue.getDocumentRule().getDocumentRuleName());
        documentRuleValueCellResponse.setDocument_rule_id(documentRuleValue.getDocumentRule().getId());
        documentRuleValueCellResponse.setValue(documentRuleValue.getRuleValue());
        return documentRuleValueCellResponse;
    }

    public ResponseEntity<ResponseObj> addRow(PositionRequestDTO positionRequestDTO){
        try{
            Long requestedId = positionRequestDTO.getPositionId();
            List<Long> requestedIdsList = Collections.singletonList(requestedId);
            Map<String, List<Long>> categorizedIds = checkAndCategorizePositions(requestedIdsList);
            List<Long> positionIdsExist = categorizedIds.get("exists");

            if (!positionIdsExist.isEmpty()) {
                String message = "Position ID " + requestedId + " already exists in the matrix.";
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObj(HttpStatus.CONFLICT.toString(), message, null));
            }

            Optional<Position> positionOpt = positionRepository.findById(requestedId);
            if (!positionOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), "Position ID not found in database: " + requestedId, null));
            }
            Position position = positionOpt.get();


            List<InputDocumentMatrix> newlyCreatedEntries = new ArrayList<>();

            List<InputDocumentMatrix> entriesToUpdate = new ArrayList<>();

            List<Document> existingDocuments = inputDocumentMatrixRepository.findAll().stream()
                    .filter(matrix -> matrix.getDocument() != null)
                    .map(InputDocumentMatrix::getDocument)
                    .distinct()
                    .collect(Collectors.toList());

            List<InputDocumentMatrix> nullPositionEntries = inputDocumentMatrixRepository.findAll().stream()
                    .filter(matrix -> matrix.getPosition() == null)
                    .collect(Collectors.toList());


            if (!existingDocuments.isEmpty()) {
                for (Document document : existingDocuments) {

                    Optional<InputDocumentMatrix> reusedEntryOpt = nullPositionEntries.stream()
                            .filter(matrix -> matrix.getDocument() != null && matrix.getDocument().getId() == (document.getId()))
                            .findFirst();

                    if (reusedEntryOpt.isPresent()) {

                        InputDocumentMatrix matrixToUpdate = reusedEntryOpt.get();
                        matrixToUpdate.setPosition(position); // Gán Position mới
                        matrixToUpdate.setUpdateAt(LocalDateTime.now());
                        matrixToUpdate.setStatusEnum(StatusEnum.Pending);
                        entriesToUpdate.add(matrixToUpdate);


                        nullPositionEntries.remove(matrixToUpdate);

                    } else {
                           InputDocumentMatrix matrix = new InputDocumentMatrix();
                        matrix.setPosition(position);
                        matrix.setDocument(document);
                        matrix.setCreateAt(LocalDateTime.now());
                        matrix.setStatusEnum(StatusEnum.Pending);
                        newlyCreatedEntries.add(matrix);
                    }
                }
            } else {
                InputDocumentMatrix matrix = new InputDocumentMatrix();
                matrix.setPosition(position);
                matrix.setCreateAt(LocalDateTime.now());
                matrix.setStatusEnum(StatusEnum.Pending);
                newlyCreatedEntries.add(matrix);
            }

            inputDocumentMatrixRepository.saveAll(entriesToUpdate);
            inputDocumentMatrixRepository.saveAll(newlyCreatedEntries);

            ResponseEntity<ResponseObj> matrixResponse = getAllMatrix();

            if (matrixResponse.getStatusCode() != HttpStatus.OK) {
                return matrixResponse;
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObj(
                            HttpStatus.CREATED.toString(),
                            "Successfully added new row for Position ID " + requestedId + " and created " + newlyCreatedEntries.size() + " cells.",
                            matrixResponse.getBody().getData()
                    )
            );

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }


    public void deleteNullEntries() {

        List<InputDocumentMatrix> nullInputdocumentMatrixList = inputDocumentMatrixRepository.findAll().stream()
                .filter(matrix -> matrix.getDocument() == null && matrix.getPosition() == null)
                .collect(Collectors.toList());

        if (!nullInputdocumentMatrixList.isEmpty()) {

            inputDocumentMatrixRepository.deleteAll(nullInputdocumentMatrixList);
        }
    }

    public ResponseEntity<ResponseObj> getAllMatrix(){
        try{
            List<InputDocumentMatrix> inputDocumentMatrixList = inputDocumentMatrixRepository.findAll();

              if (inputDocumentMatrixList.isEmpty()) {

                List<Position> allPositions = positionRepository.findAll();
                List<Document> allDocuments = documentRepository.findAll();

                if (!allPositions.isEmpty() && allDocuments.isEmpty()) {
                    List<InputDocumentMatrixResponse> matrixResponses = allPositions.stream()
                            .map(position -> convertToMatrixRowResponse(position, new ArrayList<>()))
                            .collect(Collectors.toList());

                    return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(),
                            "Matrix has rows defined but no columns. (Status 1)", matrixResponses));

                } else if (allPositions.isEmpty() && !allDocuments.isEmpty()) {
                    List<DocumentResponse> documentResponses = allDocuments.stream()
                            .map(this::convertDocumentToDocumentResponse)
                            .collect(Collectors.toList());

                    return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(),
                            "Matrix has columns defined but no rows. (Status 2)", documentResponses));

                } else {

                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                            HttpStatus.OK.toString(), "Matrix is completely empty. (Status 3)", new ArrayList<>()));
                }
            }

              deleteNullEntries();

            boolean hasDefinedColumns = inputDocumentMatrixList.stream()
                    .anyMatch(matrix -> matrix.getDocument() != null);

            List<InputDocumentMatrix> assignmentsToProcess;

             if (hasDefinedColumns) {
                assignmentsToProcess = inputDocumentMatrixList.stream()
                        .filter(matrix -> matrix.getDocument() != null || matrix.getPosition() == null)
                        .collect(Collectors.toList());
            } else {
                 assignmentsToProcess = inputDocumentMatrixList;
            }

            if (assignmentsToProcess.isEmpty()) {
                return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "Matrix data retrieved, but no assignments to display (all filtered out).", new ArrayList<>()));
            }


            List<InputDocumentMatrix> unassignedEntries = assignmentsToProcess.stream()
                    .filter(matrix -> matrix.getPosition() == null)
                    .collect(Collectors.toList());

            List<InputDocumentMatrix> assignedEntries = assignmentsToProcess.stream()
                    .filter(matrix -> matrix.getPosition() != null)
                    .collect(Collectors.toList());

            List<InputDocumentMatrixResponse> matrixResponses = new ArrayList<>();


            if (!assignedEntries.isEmpty()) {
                Map<Position, List<InputDocumentMatrix>> groupedByPosition = assignedEntries.stream()
                        .collect(Collectors.groupingBy(InputDocumentMatrix::getPosition));

                groupedByPosition.entrySet().stream()
                        .map(entry -> convertToMatrixRowResponse(entry.getKey(), entry.getValue()))
                        .forEach(matrixResponses::add);
            }


            if (!unassignedEntries.isEmpty()) {

                InputDocumentMatrixResponse unassignedRow = new InputDocumentMatrixResponse();
                unassignedRow.setPositionId(null);
                unassignedRow.setPositionName(null);

                List<DocumentCollumResponse> columnResponses = unassignedEntries.stream()
                        .map(this::convertDocumentResponse)
                        .collect(Collectors.toList());

                unassignedRow.setDocumentCollumResponseList(columnResponses);
                matrixResponses.add(unassignedRow);
            }


            return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "Successfully retrieved all matrix data", matrixResponses));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(),null));
        }
    }
    private List<InputDocumentMatrix> createMatrixCellsForNewColumn(Long documentId, Document document) {

        List<Position> allPositions = inputDocumentMatrixRepository.findAll().stream()
                .filter(position -> position != null)
                .map(InputDocumentMatrix::getPosition)
                .distinct()
                .collect(Collectors.toList());

        List<InputDocumentMatrix> newMatrixEntries = new ArrayList<>();


       if(allPositions == null
               || allPositions.isEmpty()
               || allPositions.stream().allMatch(Objects::isNull)){
           InputDocumentMatrix matrix = new InputDocumentMatrix();
           matrix.setDocument(document);
           matrix.setStatusEnum(StatusEnum.InProgress);
           matrix.setCreateAt(LocalDateTime.now());
           newMatrixEntries.add(matrix);


       }else{
           for (Position position : allPositions) {


               Optional<InputDocumentMatrix> existingCellOpt = findMatrixCell(position.getId(), documentId);

               if (!existingCellOpt.isPresent()) {

                   InputDocumentMatrix matrix = new InputDocumentMatrix();
                   matrix.setDocument(document);
                   matrix.setPosition(position);
                   matrix.setStatusEnum(StatusEnum.InProgress);
                   matrix.setCreateAt(LocalDateTime.now());
                   newMatrixEntries.add(matrix);
               }
           }
       }

        return inputDocumentMatrixRepository.saveAll(newMatrixEntries);
    }


    private Optional<InputDocumentMatrix> findMatrixCell(Long positionId, Long documentId) {
        if (documentId == null) {

            return inputDocumentMatrixRepository.findAll().stream()
                    .filter(matrix -> matrix.getPosition() != null && matrix.getPosition().getId() == positionId && matrix.getDocument() == null)
                    .findFirst();
        }

        return inputDocumentMatrixRepository.findAll().stream()
                .filter(matrix -> matrix.getPosition() != null && matrix.getPosition().getId() == positionId &&
                        matrix.getDocument() != null && matrix.getDocument().getId() == documentId)
                .findFirst();

    }

    @Transactional
    public ResponseEntity<ResponseObj> setApproveOrRejectStatus(Long departmentId, StatusEnum statusEnum, String reject_reason) {

        if (statusEnum != StatusEnum.Approve && statusEnum != StatusEnum.Reject) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(
                    HttpStatus.BAD_REQUEST.toString(), "Invalid status. Only 'Approve' or 'Reject' is allowed.", null));
        }

        try {

            List<InputDocumentMatrix> filteredMatrixList = inputDocumentMatrixRepository.findAll().stream()
                    .filter(Objects::nonNull)
                    .filter(matrix -> matrix.getPosition() != null && matrix.getPosition().getDepartment() != null)
                    .filter(matrix -> matrix.getPosition().getDepartment().getId() == departmentId)
                    .collect(Collectors.toList());

            if (filteredMatrixList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                        HttpStatus.OK.toString(), "No matrices found for Department ID " + departmentId, null));
            }

            int updatedCount = 0;
            for (InputDocumentMatrix matrix : filteredMatrixList) {

                matrix.setStatusEnum(statusEnum);
                matrix.setRejection_reason(reject_reason);

                if (statusEnum == StatusEnum.Reject) {
                    matrix.setMatrixStatusEnum(null);
                }
                // ----------------------------

                inputDocumentMatrixRepository.save(matrix);
                updatedCount++;
            }

            String msg = String.format("Successfully set status to '%s' for %d matrices in Department ID %d.",
                    statusEnum.name(), updatedCount, departmentId);



            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                    HttpStatus.OK.toString(), msg, null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error: " + e.getMessage(), null));
        }
    }

    @Transactional
    public ResponseEntity<ResponseObj> setCompleteToActive(MatrixExpirationDTO matrixExpirationDTO) {
        try {

            validateExpiration(matrixExpirationDTO);

            List<InputDocumentMatrix> inputDocumentMatrixList = inputDocumentMatrixRepository.findAll();

            if (inputDocumentMatrixList.isEmpty()) {
                String msg = "No InputDocumentMatrix entries found in the database.";
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                        HttpStatus.OK.toString(), msg, null));
            }

            int updatedCount = 0;

            for (InputDocumentMatrix matrix : inputDocumentMatrixList) {

                if (matrix.getStatusEnum() != StatusEnum.Complete) {
                    matrix.setStatusEnum(StatusEnum.Complete);
                    matrix.setActive(true);
                    updatedCount++;
                }
            }
            inputDocumentMatrixRepository.saveAll(inputDocumentMatrixList);

            String msg = String.format("Successfully set status to '%s' for %d/%d InputDocumentMatrix entries.",
                    StatusEnum.InProgress.name(), updatedCount, inputDocumentMatrixList.size());

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                    HttpStatus.OK.toString(), msg, null));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error setting matrix status to Active: " + e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObj> addColumn(DocumentRequestDTO documentRequestDTO) {
        Long documentId = documentRequestDTO.getDocumentId();
        try {

            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (!documentOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(
                        HttpStatus.NOT_FOUND.toString(), "Document ID not found in database: " + documentId, null));
            }
            Document document = documentOpt.get();

            List<Long> existPositionId = inputDocumentMatrixRepository.findAll().stream()
                    .filter(matrix -> matrix.getPosition() != null)
                    .map(matrix -> matrix.getPosition().getId())
                    .distinct()
                    .collect(Collectors.toList());
//            if(existPositionId.isEmpty()){
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Please add row first before add collum", null));
//            }

            List<Long> existingDocumentIds = inputDocumentMatrixRepository.findAll().stream()
                    .filter(matrix -> matrix.getDocument() !=null)
                    .map(matrix -> matrix.getDocument().getId())
                    .distinct()
                    .collect(Collectors.toList());

            if (existingDocumentIds.contains(documentId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObj(
                        HttpStatus.CONFLICT.toString(), "Column (Document ID) " + documentId + " already exists in the matrix.", null));
            }


            List<InputDocumentMatrix> savedEntries = createMatrixCellsForNewColumn(documentId, document);


            ResponseEntity<ResponseObj> matrixResponse = getAllMatrix();

            if (matrixResponse.getStatusCode() != HttpStatus.OK) {
                return matrixResponse;
            }

            String msg;
            if (savedEntries.isEmpty()) {

                msg = "Successfully added Document ID " + documentId + " to the matrix definition. (0 rows were processed).";
            } else {

                msg = "Successfully added Document ID " + documentId + " to " + savedEntries.size() + " existing rows.";
            }



            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObj(
                    HttpStatus.CREATED.toString(), msg, matrixResponse.getBody().getData()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public void validateExpiration(MatrixExpirationDTO dto) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new IllegalArgumentException("Time cannot be null.");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("StartDate have to before EndDate.");
        }
    }

    public ResponseEntity<ResponseObj> addColumns(List<DocumentRequestDTO> documentRequestDTOList) {
        List<Long> successfullyAddedIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        List<InputDocumentMatrix> allSavedEntries = new ArrayList<>();

        try {


            List<Long> existingDocumentIds = inputDocumentMatrixRepository.findAll().stream()
                    .filter(matrix -> matrix.getDocument() !=null)
                    .map(matrix -> matrix.getDocument().getId())
                    .distinct()
                    .collect(Collectors.toList());

            List<DocumentRequestDTO> documentsToProcess = new ArrayList<>();

            List<Position> allPositions = inputDocumentMatrixRepository.findAll().stream()
                    .filter(position -> position != null)
                    .map(InputDocumentMatrix::getPosition)
                    .distinct()
                    .collect(Collectors.toList());

            if(allPositions == null
                    || allPositions.isEmpty()
                    || allPositions.stream().allMatch(Objects::isNull)){

            }else{
                List<Long> existPositionId = inputDocumentMatrixRepository.findAll().stream()
                        .map(matrix -> matrix.getPosition().getId())
                        .distinct()
                        .collect(Collectors.toList());
            }
//            if(existPositionId.isEmpty()){
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Please add row first before add collum", null));
//            }

            for (DocumentRequestDTO dto : documentRequestDTOList) {
                if (existingDocumentIds.contains(dto.getDocumentId())) {
                    failedIds.add(dto.getDocumentId());
                } else {
                    documentsToProcess.add(dto);
                }
            }



            for (DocumentRequestDTO dto : documentsToProcess) {
                Long documentId = dto.getDocumentId();

                Optional<Document> documentOpt = documentRepository.findById(documentId);
                if (documentOpt.isPresent()) {


                    List<InputDocumentMatrix> savedEntries = createMatrixCellsForNewColumn(documentId, documentOpt.get());


                    allSavedEntries.addAll(savedEntries);
                    successfullyAddedIds.add(documentId);
                } else {
                    failedIds.add(documentId);
                }
            }


            ResponseEntity<ResponseObj> matrixResponse = getAllMatrix();


            if (matrixResponse.getStatusCode() != HttpStatus.OK) {
                return matrixResponse;
            }


            String successMsg = "Successfully added " + successfullyAddedIds.size() + " columns, creating " + allSavedEntries.size() + " cells.";
            if (!failedIds.isEmpty()) {
                successMsg += " (Ignored existing/invalid IDs: " + failedIds + ")";
            }


            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObj(
                    HttpStatus.CREATED.toString(), successMsg, matrixResponse.getBody().getData()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObj> deleteAllRow(){
        try{
            List<InputDocumentMatrix> inputDocumentMatrixList = inputDocumentMatrixRepository.findAll();
            for(InputDocumentMatrix inputDocumentMatrix : inputDocumentMatrixList){
                inputDocumentMatrix.setPosition(null);
                inputDocumentMatrix.setDeleteAt(LocalDateTime.now());
            }

            ResponseEntity<ResponseObj> getAllMatrix = getAllMatrix();

            inputDocumentMatrixRepository.saveAll(inputDocumentMatrixList);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Delete all row succesfully", getAllMatrix.getBody().getData()));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }
    public ResponseEntity<ResponseObj> deleteRow(Long positionId) {
        try {
            Optional<Position> positionOpt = positionRepository.findById(positionId);
            if (!positionOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(
                        HttpStatus.NOT_FOUND.toString(), "Position ID not found in database: " + positionId, null));
            }
            Position position = positionOpt.get();

            List<InputDocumentMatrix> entriesToDelete = inputDocumentMatrixRepository.findByPosition(position);

            if (entriesToDelete.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                        HttpStatus.OK.toString(), "Row for Position ID " + positionId + " does not exist in the matrix. Nothing to delete.", null));
            }

            for(InputDocumentMatrix inputDocumentMatrix : entriesToDelete){
                inputDocumentMatrix.setPosition(null);
                inputDocumentMatrix.setDeleteAt(LocalDateTime.now());
            }


            inputDocumentMatrixRepository.saveAll(entriesToDelete);

//            inputDocumentMatrixRepository.deleteAll(entriesToDelete);


            ResponseEntity<ResponseObj> matrixResponse = getAllMatrix();

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                    HttpStatus.OK.toString(), "Successfully deleted row for Position ID " + positionId + " and " + entriesToDelete.size() + " cells.", matrixResponse.getBody().getData()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObj> deleteColumn(Long documentId) {
        try {
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (!documentOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(
                        HttpStatus.NOT_FOUND.toString(), "Document ID not found in database: " + documentId, null));
            }
            Document document = documentOpt.get();

            List<InputDocumentMatrix> entriesToDelete = inputDocumentMatrixRepository.findByDocument(document);

            if (entriesToDelete.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                        HttpStatus.OK.toString(), "Column for Document ID " + documentId + " does not exist in the matrix. Nothing to delete.", null));
            }


            inputDocumentMatrixRepository.deleteAll(entriesToDelete);


            ResponseEntity<ResponseObj> matrixResponse = getAllMatrix();

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                    HttpStatus.OK.toString(), "Successfully deleted column for Document ID " + documentId + " and " + entriesToDelete.size() + " cells.", matrixResponse.getBody().getData()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObj> clearAllMatrixData() {
        try {
            long countBefore = inputDocumentMatrixRepository.count();
            if (countBefore == 0) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                        HttpStatus.OK.toString(), "Matrix is already empty.", null));
            }


            inputDocumentMatrixRepository.deleteAll();

              ResponseEntity<ResponseObj> matrixResponse = getAllMatrix();


            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                    HttpStatus.OK.toString(), "Successfully cleared all " + countBefore + " entries from the matrix.", matrixResponse.getBody().getData()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObj> deleteAllMatrixColumns() {
        try {

            List<InputDocumentMatrix> entriesToDelete = inputDocumentMatrixRepository.findAll().stream()
                    .filter(matrix -> matrix.getDocument() != null)
                    .collect(Collectors.toList());

            if (entriesToDelete.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                        HttpStatus.OK.toString(), "No defined columns found in the matrix. Nothing to delete.", null));
            }

            long deletedCount = entriesToDelete.size();


            inputDocumentMatrixRepository.deleteAll(entriesToDelete);


            ResponseEntity<ResponseObj> matrixResponse = getAllMatrix();

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                    HttpStatus.OK.toString(), "Successfully deleted all " + deletedCount + " cells, effectively removing all defined columns.", matrixResponse.getBody().getData()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }



    public ResponseEntity<ResponseObj> clickToCell(List<Long> matrixId){
        try {
            List<InputDocumentMatrix> inputDocumentMatrixList = inputDocumentMatrixRepository.findAllById(matrixId);

            for(InputDocumentMatrix inputDocumentMatrix : inputDocumentMatrixList){
                boolean newRequiredStatus = !inputDocumentMatrix.isRequired();

                inputDocumentMatrix.setRequired(newRequiredStatus);
                inputDocumentMatrix.setUpdateAt(LocalDateTime.now());
            }

            inputDocumentMatrixRepository.saveAll(inputDocumentMatrixList);


            ResponseEntity<ResponseObj> matrixResponse = getAllMatrix();

            return ResponseEntity.ok(new ResponseObj(
                    HttpStatus.OK.toString(),
                    "Successfully toggled required status for selected matrix cells.",
                    matrixResponse.getBody().getData()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObj> deleteAllMatrixRowsAndColumns() {
        try {
            long countBefore = inputDocumentMatrixRepository.count();
            if (countBefore == 0) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                        HttpStatus.OK.toString(), "Matrix is already empty.", null));
            }


            inputDocumentMatrixRepository.deleteAll();

              ResponseEntity<ResponseObj> matrixResponse = getAllMatrix();


            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(
                    HttpStatus.OK.toString(), "Successfully deleted all " + countBefore + " entries, clearing the entire matrix (all rows and columns).", matrixResponse.getBody().getData()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObj> setPendingStatusMatrix(TimeMatrixDTO timeMatrixDTO){
        try{

            LocalDateTime currentTime = LocalDateTime.now();

           if (timeMatrixDTO.getStartDate_deadLine()  == null || timeMatrixDTO.getEndDate_deadLine() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(
                        HttpStatus.BAD_REQUEST.toString(),
                        "Start Date dead line and End Date dead line cannot be empty.",
                        null));
            }


            if (timeMatrixDTO.getStartDate_deadLine().isAfter(timeMatrixDTO.getEndDate_deadLine())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(
                        HttpStatus.BAD_REQUEST.toString(),
                        "Start Date dead line must be before or the same as End Date dead line.",
                        null));
            }
      if (!timeMatrixDTO.getStartDate_deadLine().isAfter(currentTime)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(
                        HttpStatus.BAD_REQUEST.toString(),
                        "Start Date dead line must occur after the current time.",
                        null));
            }


            List<InputDocumentMatrix> inputDocumentMatrixList = inputDocumentMatrixRepository.findAll();

            if (inputDocumentMatrixList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new  ResponseObj(
                        HttpStatus.OK.toString(), "Matrix is currently empty, no status was updated.", null));
            }

            for(InputDocumentMatrix inputDocumentMatrix : inputDocumentMatrixList){
                inputDocumentMatrix.setStartDate_deadLine(timeMatrixDTO.getStartDate_deadLine());
                inputDocumentMatrix.setEndDate_deadLine(timeMatrixDTO.getEndDate_deadLine());
                inputDocumentMatrix.setStatusEnum(StatusEnum.InProgress);
                inputDocumentMatrix.setActive(true);
            }

            inputDocumentMatrixRepository.saveAll(inputDocumentMatrixList);

            Set<Department> affectedDepartments = inputDocumentMatrixList.stream()
                    .map(InputDocumentMatrix::getPosition)
                    .filter(Objects::nonNull)
                    .map(Position::getDepartment)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            List<Account> headOfDepartments = accountRepository.findAll().stream()
                    .filter(acc -> acc.getRole().getRoleName() == RoleEnum.HEAD_OF_DEPARTMENT)
                    .filter(acc -> acc.getDepartment() != null && affectedDepartments.contains(acc.getDepartment()))
                    .collect(Collectors.toList());

            NotificationDTO notification = new NotificationDTO(
                    "Ma Trận Hồ Sơ Đầu Vào Đã Được Kích Hoạt",
                    "Thời gian nộp hồ sơ: " + timeMatrixDTO.getStartDate_deadLine().toLocalDate() + " đến " + timeMatrixDTO.getEndDate_deadLine().toLocalDate() +
                            "\nVui lòng vào hệ thống ngay để thiết lập quy tắc tài liệu cho các vị trí thuộc khoa của bạn.",
                    "matrix_pending",
                    LocalDateTime.now(),
                    "/admin/matrix"
            );

            headOfDepartments.forEach(head ->
                    notificationService.sendToUser(head.getId(), notification)
            );

            ResponseEntity<ResponseObj> matrixResponse = getAllMatrix();

            return ResponseEntity.ok(new ResponseObj(
                    HttpStatus.OK.toString(),
                    "Successfully updated PENDING status and Start/End Dates dead line for the matrix.",
                    matrixResponse.getBody().getData()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObj> clickToSellRequired(CellMatrixDTO cellMatrixDTO){
        try{
            Optional<InputDocumentMatrix> inputDocumentMatrix = inputDocumentMatrixRepository.findById(cellMatrixDTO.getMatrixId());
            inputDocumentMatrix.get().setRequired(cellMatrixDTO.isRequired());
            if(inputDocumentMatrix.get().getDocumentRuleValueList().isEmpty() && cellMatrixDTO.isRequired() == true ){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "If you click to choose document for position you have to define rule value of document", null));
            }
            inputDocumentMatrixRepository.save(inputDocumentMatrix.get());

            List<DocumentRuleValue> documentRuleValueList = new ArrayList<>();

            if(cellMatrixDTO.isRequired() == false && !inputDocumentMatrix.get().getDocumentRuleValueList().isEmpty()){
                for(DocumentRuleValue documentRuleValue : inputDocumentMatrix.get().getDocumentRuleValueList()){
                    documentRuleValue.setActive(false);
                    documentRuleValueList.add(documentRuleValue);
                }
                documentRuleValueRepository.saveAll(documentRuleValueList);
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Click to cell matrix succesfully", null));
        }catch (Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    private Map<Long, String> getDepartmentNames() {
        return departmentRepository.findAll().stream()
                .collect(Collectors.toMap(Department::getId, Department::getDepartmentName));
    }

    @Transactional
    public ResponseEntity<ResponseObj> setDraftedStatus(Long departmentID) {
        try {

            List<InputDocumentMatrix> matricesToUpdate = inputDocumentMatrixRepository.findAll().stream()
                    .filter(m -> m.getPosition() != null)
                    .filter(m -> m.getPosition().getDepartment() != null)
                    .filter(m -> m.getPosition().getDepartment().getId() == departmentID)
                    .collect(Collectors.toList());


            if (matricesToUpdate.isEmpty()) {

                String message = String.format("Không tìm thấy ma trận nào cần thiết lập DRAFTED cho Khoa ID %d (Hoặc tất cả đã có trạng thái).", departmentID);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_GATEWAY.toString(), message, null));
            } else {

                for (InputDocumentMatrix inputDocumentMatrix : matricesToUpdate) {
                    inputDocumentMatrix.setMatrixStatusEnum(MatrixStatusEnum.Drafted);
                }


                inputDocumentMatrixRepository.saveAll(matricesToUpdate);

                int updatedCount = matricesToUpdate.size();
                String successMessage = String.format("Đã cập nhật %d ma trận thuộc Khoa ID %d sang trạng thái DRAFTED.", updatedCount, departmentID);

                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), successMessage, null));
            }

        } catch (Exception e) {

            String errorMessage = "Lỗi hệ thống khi thiết lập trạng thái DRAFTED: " + e.getMessage();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), errorMessage, null));
        }
    }


    public MatrixDashBoardResponse getMatrixApprovalProgressByDepartment() {

        List<InputDocumentMatrix> allMatrices = inputDocumentMatrixRepository.findAll();


        Map<String, List<InputDocumentMatrix>> matricesByDept = allMatrices.stream()
                .filter(m -> m.getPosition() != null && m.getPosition().getDepartment() != null)
                .collect(Collectors.groupingBy(m -> m.getPosition().getDepartment().getDepartmentName()));

        long approvedDepts = 0;
        long rejectedDepts = 0;
        long draftedDepts = 0;
        long inProgressDepts = 0;


        for (List<InputDocumentMatrix> deptMatrices : matricesByDept.values()) {
            boolean isAllApproved = true;
            boolean hasReject = false;
            boolean hasDraft = false;
            boolean hasNull = false;

            for (InputDocumentMatrix m : deptMatrices) {

                if (m.getStatusEnum() != StatusEnum.Approve) {
                    isAllApproved = false;
                }
                if (m.getStatusEnum() == StatusEnum.Reject) {
                    hasReject = true;
                }


                if (m.getMatrixStatusEnum() == MatrixStatusEnum.Drafted) {
                    hasDraft = true;
                } else if (m.getMatrixStatusEnum() == null) {

                    hasNull = true;
                }
            }

            if (isAllApproved) {
                approvedDepts++;
            } else if (hasReject) {
                rejectedDepts++;
            } else if (hasDraft) {
                draftedDepts++;
            } else {

                inProgressDepts++;
            }
        }


        int totalDepts = matricesByDept.size();
        double percentage = 0.0;
        if (totalDepts > 0) {

            percentage = ((double) approvedDepts / totalDepts) * 100.0;
            percentage = Math.round(percentage * 100.0) / 100.0;
        }

        return new MatrixDashBoardResponse(
                approvedDepts,
                rejectedDepts,
                draftedDepts,
                inProgressDepts,
                percentage
        );
    }

    public ResponseEntity<ResponseObj> filterMatrixByDeptAndPos(Long departmentId, Long positionId) {
        try {
            List<InputDocumentMatrix> filteredEntries = inputDocumentMatrixRepository.filterMatrix(departmentId, positionId);

            if (filteredEntries.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj( HttpStatus.BAD_REQUEST.toString(),
                        "No data found for the selected filter.",
                        new ArrayList<>()));
            }


            Map<Position, List<InputDocumentMatrix>> groupedByPosition = filteredEntries.stream()
                    .collect(Collectors.groupingBy(InputDocumentMatrix::getPosition));

            List<InputDocumentMatrixResponse> matrixResponses = groupedByPosition.entrySet().stream()
                    .map(entry -> convertToMatrixRowResponse(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ResponseObj(
                    HttpStatus.OK.toString(),
                    "Successfully filtered matrix data",
                    matrixResponses));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "Error filtering matrix: " + e.getMessage(),
                    null));
        }
    }

    public List<InputDocumentMatrixResponse> getMatrixDetails(List<Long> matrixIds) {

        List<InputDocumentMatrix> matrixEntities = inputDocumentMatrixRepository.findAllDetailsByIds(matrixIds);


        Map<Long, List<InputDocumentMatrix>> groupedByPosition = matrixEntities.stream()
                .collect(Collectors.groupingBy(m -> m.getPosition().getId()));

        return groupedByPosition.values().stream().map(entities -> {
            InputDocumentMatrix first = entities.get(0);

            InputDocumentMatrixResponse response = new InputDocumentMatrixResponse();
            response.setPositionId(first.getPosition().getId());
            response.setPositionName(first.getPosition().getPositionName());
            response.setStatusEnum(first.getStatusEnum());
            response.setMatrixStatusEnum(first.getMatrixStatusEnum());
            response.setStartDate(first.getStartDate_deadLine());
            response.setEndDate(first.getEndDate_deadLine());
            response.setReject_reason(first.getRejection_reason());
            response.setDepartmentId(first.getPosition().getDepartment().getId());

            List<DocumentCollumResponse> columnResponses = entities.stream().map(m -> {
                DocumentCollumResponse col = new DocumentCollumResponse();
                col.setDocument_id(m.getDocument().getId());
                col.setDocument_name(m.getDocument().getDocumentName());
                col.setStatusEnum(m.getStatusEnum());
                col.setRequired(m.isRequired());
                col.setMatrixId(m.getId());


                List<DocumentRuleValueCellResponse> ruleValues = m.getDocumentRuleValueList().stream()
                        .map(rv -> new DocumentRuleValueCellResponse(
                                rv.getId(),
                                rv.getRuleValue(),
                                rv.getDocumentRule().getId(),
                                rv.getDocumentRule().getDocumentRuleName()
                        )).collect(Collectors.toList());

                col.setDocumentRuleValueList(ruleValues);
                return col;
            }).collect(Collectors.toList());

            response.setDocumentCollumResponseList(columnResponses);
            return response;
        }).collect(Collectors.toList());
    }

}