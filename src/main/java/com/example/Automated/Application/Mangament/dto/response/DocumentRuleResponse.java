package com.example.Automated.Application.Mangament.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class DocumentRuleResponse {
    private Long documentId;
    private String documentName;
    private Long documentRuleId;
    private String documentRuleName;
    private String documentRuleDescription;

    public Long getDocumentRuleId() {
        return documentRuleId;
    }

    public void setDocumentRuleId(Long documentRuleId) {
        this.documentRuleId = documentRuleId;
    }

    public String getDocumentRuleName() {
        return documentRuleName;
    }

    public void setDocumentRuleName(String documentRuleName) {
        this.documentRuleName = documentRuleName;
    }

    public String getDocumentRuleDescription() {
        return documentRuleDescription;
    }

    public void setDocumentRuleDescription(String documentRuleDescription) {
        this.documentRuleDescription = documentRuleDescription;
    }
}
