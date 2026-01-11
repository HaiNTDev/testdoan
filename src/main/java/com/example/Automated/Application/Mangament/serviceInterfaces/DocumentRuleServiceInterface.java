package com.example.Automated.Application.Mangament.serviceInterfaces;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.dto.request.DocumentRuleDTO;
import org.springframework.http.ResponseEntity;

public interface DocumentRuleServiceInterface {
    ResponseEntity<ResponseObj> createDocumentRule(DocumentRuleDTO documentRuleDTO);

    ResponseEntity<ResponseObj> getAllDocumentRules();

    ResponseEntity<ResponseObj> getDocumentRuleById(Long id);

    ResponseEntity<ResponseObj> updateDocumentRule(Long id, DocumentRuleDTO documentRuleUpdateDTO);

    ResponseEntity<ResponseObj> deleteDocumentRule(Long id);
}
