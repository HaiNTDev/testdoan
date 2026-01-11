package com.example.Automated.Application.Mangament.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private String documentName;
    private String documentDescription;

    public String getDocumentDescription() {
        return documentDescription;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
}


