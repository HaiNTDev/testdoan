package com.example.Automated.Application.Mangament.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Data
@Getter
@Setter
@NoArgsConstructor
public class CreateDocumentWithRulesDTO {
    private String documentName;
    private String documentDescription;
    private List<DocumentRuleDTO> documentRules;

    public List<DocumentRuleDTO> getDocumentRules() {
        return documentRules;
    }

    public void setDocumentRules(List<DocumentRuleDTO> documentRules) {
        this.documentRules = documentRules;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }
}
