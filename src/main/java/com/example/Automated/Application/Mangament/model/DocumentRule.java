package com.example.Automated.Application.Mangament.model;

import jakarta.persistence.*;
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
public class DocumentRule extends BaseEntity{
    private String documentRuleName;

    @Column(columnDefinition = "TEXT")
    private String documentRuleDescription;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @OneToMany(mappedBy = "documentRule", fetch = FetchType.LAZY)
    private List<DocumentRuleValue> documentRuleValueList;
}
