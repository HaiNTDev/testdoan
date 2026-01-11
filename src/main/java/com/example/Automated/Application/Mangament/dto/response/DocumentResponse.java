package com.example.Automated.Application.Mangament.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DocumentResponse {
    private Long id;
    private String documentName;
    private String documentDescription;

    public DocumentResponse(Long id, String documentName, String documentDescription) {
        this.id = id;
        this.documentName = documentName;
        this.documentDescription = documentDescription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
