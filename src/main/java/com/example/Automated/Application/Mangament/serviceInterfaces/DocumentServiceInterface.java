package com.example.Automated.Application.Mangament.serviceInterfaces;

import com.example.Automated.Application.Mangament.dto.request.CreateDocumentWithRulesDTO;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.dto.request.DocumentDTO;
import org.springframework.http.ResponseEntity;

public interface DocumentServiceInterface {
    ResponseEntity<ResponseObj> createDocument(DocumentDTO documentDTO);

    ResponseEntity<ResponseObj> getAllDocuments();

    ResponseEntity<ResponseObj> getDocumentById(Long id);

    ResponseEntity<ResponseObj> updateDocument(Long id, DocumentDTO documentUpdateDTO);

    ResponseEntity<ResponseObj> deleteDocument(Long id);

    ResponseEntity<ResponseObj> createDocumentWithRules(CreateDocumentWithRulesDTO request);

    ResponseEntity<ResponseObj> getAllDocumentsWithRules();
    ResponseEntity<ResponseObj> getDocumentWithRulesById(Long id);
}
