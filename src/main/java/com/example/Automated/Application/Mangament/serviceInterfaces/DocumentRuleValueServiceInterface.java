package com.example.Automated.Application.Mangament.serviceInterfaces;

import com.example.Automated.Application.Mangament.dto.request.RuleCellCreationDTO;
import com.example.Automated.Application.Mangament.dto.request.RuleCellUpdateDTO;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public interface DocumentRuleValueServiceInterface {
    ResponseEntity<ResponseObj> createDocumentRuleValue(RuleCellCreationDTO ruleCellCreationDTO);
    ResponseEntity<ResponseObj> updateDocumentRuleValue(RuleCellUpdateDTO ruleCellUpdateDTO);
}
