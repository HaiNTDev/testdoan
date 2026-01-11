package com.example.Automated.Application.Mangament.Controller;

import com.example.Automated.Application.Mangament.dto.request.CreateDocumentWithRulesDTO;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.dto.request.DocumentDTO;
import com.example.Automated.Application.Mangament.serviceInterfaces.DocumentServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/documents")
public class DocumentController {
    @Autowired
    private DocumentServiceInterface documentServiceInterface;

    @PostMapping("/create")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> createDocument(@RequestBody DocumentDTO request) {
        return documentServiceInterface.createDocument(request);
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> getAllDocuments() {
        return documentServiceInterface.getAllDocuments();
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> getDocumentById(@PathVariable Long id) {
        return documentServiceInterface.getDocumentById(id);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> updateDocument(@PathVariable Long id, @RequestBody DocumentDTO request) {
        return documentServiceInterface.updateDocument(id, request);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> deleteDocument(@PathVariable Long id) {
        return documentServiceInterface.deleteDocument(id);
    }
    @PostMapping("/create-with-rules")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> createDocumentWithRules(@RequestBody CreateDocumentWithRulesDTO request) {
        return documentServiceInterface.createDocumentWithRules(request);
    }
    @GetMapping("/all-with-rules")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> getAllDocumentsWithRules() {
        return documentServiceInterface.getAllDocumentsWithRules();
    }
    @GetMapping("/{id}/with-rules")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> getDocumentWithRulesById(@PathVariable Long id) {
        return documentServiceInterface.getDocumentWithRulesById(id);
    }
}
