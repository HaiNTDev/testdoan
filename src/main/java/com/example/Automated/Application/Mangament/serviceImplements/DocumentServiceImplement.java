package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.dto.request.CreateDocumentWithRulesDTO;
import com.example.Automated.Application.Mangament.dto.request.DocumentRuleDTO;
import com.example.Automated.Application.Mangament.dto.response.*;
import com.example.Automated.Application.Mangament.dto.request.DocumentDTO;
import com.example.Automated.Application.Mangament.exception.AppException;
import com.example.Automated.Application.Mangament.exception.ErrorCode;
import com.example.Automated.Application.Mangament.model.Document;
import com.example.Automated.Application.Mangament.model.DocumentRule;
import com.example.Automated.Application.Mangament.repositories.DocumentRepository;
import com.example.Automated.Application.Mangament.repositories.DocumentRuleRepository;
import com.example.Automated.Application.Mangament.serviceInterfaces.DocumentServiceInterface;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImplement implements DocumentServiceInterface {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentRuleRepository documentRuleRepository;

    @Override
    @Transactional
    public ResponseEntity<ResponseObj> createDocument(DocumentDTO request) {
        try {
            Document document = new Document();
            document.setDocumentName(request.getDocumentName());
            document.setDocumentDescription(request.getDocumentDescription());
            document.setActive(true);

            document = documentRepository.save(document);
            DocumentResponse response = mapToResponse(document);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObj(HttpStatus.CREATED.toString(), "Document created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error creating document: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ResponseObj> getAllDocuments() {
        try {
            List<Document> documents = documentRepository.findAll();
            if (documents.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "List is empty", null));
            }
            List<DocumentResponse> responses = documents.stream()
                    .filter(Document::isActive)
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "List of documents", responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Server error: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ResponseObj> getDocumentById(Long id) {
        try {
            Document document = documentRepository.findById(id)
                    .filter(Document::isActive)
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));
            DocumentResponse response = mapToResponse(document);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Document found", response));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Server error: " + e.getMessage(), null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObj> updateDocument(Long id, DocumentDTO request) {
        try {
            Document document = documentRepository.findById(id)
                    .filter(Document::isActive)
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));

            if (request.getDocumentName() != null) document.setDocumentName(request.getDocumentName());
            if (request.getDocumentDescription() != null) document.setDocumentDescription(request.getDocumentDescription());

            document = documentRepository.save(document);
            DocumentResponse response = mapToResponse(document);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Document updated successfully", response));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error updating document: " + e.getMessage(), null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObj> deleteDocument(Long id) {
        try {
            Document document = documentRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));
            document.setActive(false);
            documentRepository.save(document);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Document deleted successfully", null));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error deleting document: " + e.getMessage(), null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObj> createDocumentWithRules(CreateDocumentWithRulesDTO request) {
        try {
            // Kiểm tra Document tồn tại chưa (by name ignore case)
            if (documentRepository.findByDocumentNameIgnoreCase(request.getDocumentName()).isPresent()) {
                throw new AppException(ErrorCode.UNCATEGORIZED_ERROR); // Custom error: Document exists
            }

            Document document = new Document();
            document.setDocumentName(request.getDocumentName());
            document.setDocumentDescription(request.getDocumentDescription());
            document.setActive(true);
            document = documentRepository.save(document);

            List<DocumentRuleSlimResponse> ruleResponses = new ArrayList<>();
            if (request.getDocumentRules() != null) {
                for (DocumentRuleDTO ruleDTO : request.getDocumentRules()) {
                    DocumentRule rule = new DocumentRule();
                    rule.setDocumentRuleName(ruleDTO.getDocumentRuleName());
                    rule.setDocumentRuleDescription(ruleDTO.getDocumentRuleDescription());
                    rule.setDocument(document);
                    rule.setActive(true);
                    rule = documentRuleRepository.save(rule);
                    ruleResponses.add(mapRuleToSlimResponse(rule));
                }
            }

            DocumentWithRulesResponse response = mapToDocumentWithRulesResponse(document, ruleResponses);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObj(HttpStatus.CREATED.toString(), "Document with rules created successfully", response));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error creating document with rules: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ResponseObj> getAllDocumentsWithRules() {
        try {
            List<Document> documents = documentRepository.findAll().stream()
                    .filter(Document::isActive)
                    .collect(Collectors.toList());

            if (documents.isEmpty()) {
                return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "No documents found", null));
            }

            List<DocumentWithRulesResponse> responses = documents.stream()
                    .map(doc -> {
                        List<DocumentRuleSlimResponse> rules = doc.getDocumentRuleList().stream()
                                .filter(DocumentRule::isActive)
                                .map(this::mapRuleToSlimResponse)
                                .collect(Collectors.toList());
                        return mapToDocumentWithRulesResponse(doc, rules);
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "List of documents with rules", responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Server error: " + e.getMessage(), null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObj> getDocumentWithRulesById(Long id) {
        try {
            Document document = documentRepository.findById(id)
                    .filter(Document::isActive)
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));

            List<DocumentRuleSlimResponse> rules = document.getDocumentRuleList().stream()
                    .filter(DocumentRule::isActive)
                    .map(this::mapRuleToSlimResponse)
                    .collect(Collectors.toList());

            DocumentWithRulesResponse response = mapToDocumentWithRulesResponse(document, rules);
            return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "Document with rules found", response));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Server error: " + e.getMessage(), null));
        }
    }
    private DocumentWithRulesResponse mapToDocumentWithRulesResponse(Document document, List<DocumentRuleSlimResponse> rules) {
        DocumentWithRulesResponse response = new DocumentWithRulesResponse();
        response.setDocumentId(document.getId());
        response.setDocumentName(document.getDocumentName());
        response.setDocumentDescription(document.getDocumentDescription());
        response.setDocumentRules(rules);
        return response;
    }
    private DocumentRuleSlimResponse mapRuleToSlimResponse(DocumentRule rule) {
        DocumentRuleSlimResponse response = new DocumentRuleSlimResponse();
        response.setDocumentRuleId(rule.getId());
        response.setDocumentRuleName(rule.getDocumentRuleName());
        response.setDocumentRuleDescription(rule.getDocumentRuleDescription());
        return response;
    }
//    private DocumentRuleResponse mapRuleToResponse(DocumentRule rule) {
//        DocumentRuleResponse response = new DocumentRuleResponse();
//        response.setDocumentRuleId(rule.getId());
//        response.setDocumentRuleName(rule.getDocumentRuleName());
//        response.setDocumentRuleDescription(rule.getDocumentRuleDescription());
//        return response;
//    }
    private DocumentResponse mapToResponse(Document document) {
        DocumentResponse response = new DocumentResponse();
        response.setId(document.getId());
        response.setDocumentName(document.getDocumentName());
        response.setDocumentDescription(document.getDocumentDescription());
        return response;
    }
}
