package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.dto.request.DocumentRuleDTO;
import com.example.Automated.Application.Mangament.dto.response.DocumentRuleResponse;
import com.example.Automated.Application.Mangament.exception.AppException;
import com.example.Automated.Application.Mangament.exception.ErrorCode;
import com.example.Automated.Application.Mangament.model.Document;
import com.example.Automated.Application.Mangament.model.DocumentRule;

import com.example.Automated.Application.Mangament.repositories.DocumentRepository;
import com.example.Automated.Application.Mangament.repositories.DocumentRuleRepository;
import com.example.Automated.Application.Mangament.serviceInterfaces.DocumentRuleServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentRuleServiceImplement implements DocumentRuleServiceInterface{
    @Autowired
    private DocumentRuleRepository documentRuleRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    @Transactional
    public ResponseEntity<ResponseObj> createDocumentRule(DocumentRuleDTO request) {
        try {
            Document document = null;
            if (request.getDocumentId() != null) {
                document = documentRepository.findById(request.getDocumentId())
                        .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));
            }
            DocumentRule documentRule = new DocumentRule();
            documentRule.setDocumentRuleName(request.getDocumentRuleName());
            documentRule.setDocumentRuleDescription(request.getDocumentRuleDescription());
            documentRule.setDocument(document);
            documentRule.setActive(true);
            documentRule = documentRuleRepository.save(documentRule);
            DocumentRuleResponse response = mapToResponse(documentRule);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObj(HttpStatus.CREATED.toString(), "Document rule created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error creating document rule: " + e.getMessage(), null));
        }
    }
    @Override
    public ResponseEntity<ResponseObj> getAllDocumentRules() {
        try {
            List<DocumentRule> documentRules = documentRuleRepository.findAll();
            if (documentRules.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "List is empty", null));
            }
            List<DocumentRuleResponse> responses = documentRules.stream()
                    .filter(DocumentRule::isActive)
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "List of document rules", responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Server error: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ResponseObj> getDocumentRuleById(Long id) {
        try {
            DocumentRule documentRule = documentRuleRepository.findById(id)
                    .filter(DocumentRule::isActive)
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));
            DocumentRuleResponse response = mapToResponse(documentRule);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Document rule found", response));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Server error: " + e.getMessage(), null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObj> updateDocumentRule(Long id, DocumentRuleDTO request) {
        try {
            DocumentRule documentRule = documentRuleRepository.findById(id)
                    .filter(DocumentRule::isActive)
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));

            if (request.getDocumentRuleName() != null) documentRule.setDocumentRuleName(request.getDocumentRuleName());
            if (request.getDocumentRuleDescription() != null) documentRule.setDocumentRuleDescription(request.getDocumentRuleDescription());
            if (request.getDocumentId() != null) {
                Document document = documentRepository.findById(request.getDocumentId())
                        .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));
                documentRule.setDocument(document);
            }

            documentRule = documentRuleRepository.save(documentRule);
            DocumentRuleResponse response = mapToResponse(documentRule);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Document rule updated successfully", response));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error updating document rule: " + e.getMessage(), null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObj> deleteDocumentRule(Long id) {
        try {
            DocumentRule documentRule = documentRuleRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));
            documentRule.setActive(false);
            documentRuleRepository.save(documentRule);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Document rule deleted successfully", null));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error deleting document rule: " + e.getMessage(), null));
        }
    }

    private DocumentRuleResponse mapToResponse(DocumentRule documentRule) {
        DocumentRuleResponse response = new DocumentRuleResponse();
        response.setDocumentId(documentRule.getDocument().getId());
        response.setDocumentName(documentRule.getDocument().getDocumentName());
        response.setDocumentRuleId(documentRule.getId());
        response.setDocumentRuleName(documentRule.getDocumentRuleName());
        response.setDocumentRuleDescription(documentRule.getDocumentRuleDescription());
        return response;
    }
}
