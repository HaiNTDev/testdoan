package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.dto.request.DocumentRuleValueDTO;
import com.example.Automated.Application.Mangament.dto.request.DocumentRuleValueUpdateDTO;
import com.example.Automated.Application.Mangament.dto.request.RuleCellCreationDTO;
import com.example.Automated.Application.Mangament.dto.request.RuleCellUpdateDTO;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.model.DocumentRule;
import com.example.Automated.Application.Mangament.model.DocumentRuleValue;
import com.example.Automated.Application.Mangament.model.InputDocumentMatrix;
import com.example.Automated.Application.Mangament.repositories.DocumentRuleRepository;
import com.example.Automated.Application.Mangament.repositories.DocumentRuleValueRepository;
import com.example.Automated.Application.Mangament.repositories.InputDocumentMatrixRepository;
import com.example.Automated.Application.Mangament.serviceInterfaces.DocumentRuleValueServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentRuleValueServiceImplement implements DocumentRuleValueServiceInterface {
    @Autowired
    InputDocumentMatrixRepository inputDocumentMatrixRepository;

    @Autowired
    DocumentRuleValueRepository documentRuleValueRepository;

    @Autowired
    DocumentRuleRepository documentRuleRepository;

    public ResponseEntity<ResponseObj> createDocumentRuleValue(RuleCellCreationDTO ruleCellCreationDTO){
        try{
            List<DocumentRuleValue> documentRuleValueList = new ArrayList<>();
            InputDocumentMatrix inputDocumentMatrix = inputDocumentMatrixRepository.findById(ruleCellCreationDTO.getMatrixID()).get();
            if(inputDocumentMatrix == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), "input document matrix rule does not exist", null));
            }
            for(DocumentRuleValueDTO documentRuleValueDTO : ruleCellCreationDTO.getDocumentRuleValueDTOList()){
                DocumentRule documentRule = documentRuleRepository.findById(documentRuleValueDTO.getDocument_rule_Id()).get();
                if(documentRule == null ){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), "document rule does not exist", null));
                }
                DocumentRuleValue documentRuleValue = new DocumentRuleValue();
                documentRuleValue.setRuleValue(documentRuleValueDTO.getDocument_rule_value());
                documentRuleValue.setInputDocumentMatrix(inputDocumentMatrix);
                documentRuleValue.setDocumentRule(documentRule);
                documentRuleValue.setActive(true);
                documentRuleValue.setCreateAt(LocalDateTime.now());
                documentRuleValueList.add(documentRuleValue);
            }
            documentRuleValueRepository.saveAll(documentRuleValueList);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Create document rule value succesfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(),null));
        }

    }

    public ResponseEntity<ResponseObj> updateDocumentRuleValue(RuleCellUpdateDTO ruleCellUpdateDTO){
        try{
            InputDocumentMatrix inputDocumentMatrix = inputDocumentMatrixRepository.findById(ruleCellUpdateDTO.getMatrixId()).get();
            if(inputDocumentMatrix == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), "input document matrix rule does not exist", null));
            }
            List<DocumentRuleValue> documentRuleValueLisT = documentRuleValueRepository.findAll();
            for(DocumentRuleValue documentRuleValue : documentRuleValueLisT){
                for(DocumentRuleValueUpdateDTO documentRuleValueUpdateDTO : ruleCellUpdateDTO.getDocumentRuleValueUpdateDTOList()){
                    if(documentRuleValue.getId() == documentRuleValueUpdateDTO.getDocument_rule_value_id()){
                        documentRuleValue.setRuleValue(documentRuleValueUpdateDTO.getRule_value());
                        documentRuleValue.setUpdateAt(LocalDateTime.now());
                    }
                }
            }
            documentRuleValueRepository.saveAll(documentRuleValueLisT);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Update document rule value succesfuly", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }
}
