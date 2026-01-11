package com.example.Automated.Application.Mangament.Controller;

import com.example.Automated.Application.Mangament.dto.request.RuleCellCreationDTO;
import com.example.Automated.Application.Mangament.dto.request.RuleCellUpdateDTO;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.serviceInterfaces.DocumentRuleValueServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/document_rule_value")
public class DocumentRuleValueController {
    @Autowired
    DocumentRuleValueServiceInterface documentRuleValueServiceInterface;

    @PostMapping("/create_document_rule_value")
    public ResponseEntity<ResponseObj> createDocumentRuleValue(@RequestBody RuleCellCreationDTO ruleCellCreationDTO){
        return documentRuleValueServiceInterface.createDocumentRuleValue(ruleCellCreationDTO);
    }

    @PutMapping("/update_document_rule_value")
    public ResponseEntity<ResponseObj> updateDocumentRuleValue(@RequestBody RuleCellUpdateDTO ruleCellCellUpdateDTO){
        return documentRuleValueServiceInterface.updateDocumentRuleValue(ruleCellCellUpdateDTO);
    }
}
