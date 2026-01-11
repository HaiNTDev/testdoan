package com.example.Automated.Application.Mangament.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Data
@Getter
@Setter
@NoArgsConstructor
public class DocumentRuleDTO {
    @NotBlank(message = "Document rule name is required")
    private String documentRuleName;
    private String documentRuleDescription;
    private Long documentId;

    public DocumentRuleDTO(String documentRuleName, String documentRuleDescription, Long documentId) {
        this.documentRuleName = documentRuleName;
        this.documentRuleDescription = documentRuleDescription;
        this.documentId = documentId;
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

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }
}
