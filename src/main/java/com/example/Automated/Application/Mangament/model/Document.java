package com.example.Automated.Application.Mangament.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Document extends BaseEntity{

    private String documentName;


    @Column(columnDefinition = "Text")
    private String documentDescription;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<InputDocumentMatrix> inputDocumentMatrixList;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<DocumentRule> documentRuleList;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Trainee_Document_Submission> traineeDocumentSubmissionList;
}
