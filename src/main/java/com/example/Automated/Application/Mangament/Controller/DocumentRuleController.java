package com.example.Automated.Application.Mangament.Controller;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.dto.request.DocumentRuleDTO;
import com.example.Automated.Application.Mangament.serviceInterfaces.DocumentRuleServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/document-rules")
public class DocumentRuleController {


    @Autowired
    private DocumentRuleServiceInterface documentRuleServiceInterface;

    @PostMapping("/create")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> createDocumentRule(@RequestBody DocumentRuleDTO request) {
        return documentRuleServiceInterface.createDocumentRule(request);
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> getAllDocumentRules() {
        return documentRuleServiceInterface.getAllDocumentRules();
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> getDocumentRuleById(@PathVariable Long id) {
        return documentRuleServiceInterface.getDocumentRuleById(id);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> updateDocumentRule(@PathVariable Long id, @RequestBody DocumentRuleDTO request) {
        return documentRuleServiceInterface.updateDocumentRule(id, request);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> deleteDocumentRule(@PathVariable Long id) {
        return documentRuleServiceInterface.deleteDocumentRule(id);
    }
}
