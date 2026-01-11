package com.example.Automated.Application.Mangament.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class DocumentRuleValue extends BaseEntity{
    @Column(columnDefinition = "TEXT")
    private String ruleValue;

    @ManyToOne
    @JoinColumn(name = "input_document_matrix_id")
    private InputDocumentMatrix inputDocumentMatrix;

    @ManyToOne
    @JoinColumn(name = "document_rule_id")
    private DocumentRule documentRule;
}
