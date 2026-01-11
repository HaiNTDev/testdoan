package com.example.Automated.Application.Mangament.model;


import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Trainee_Document_Submission extends BaseEntity{
    @Enumerated(EnumType.STRING)
    private StatusEnum statusEnum;

    private String trainee_document_name;

    private String take_note;



    @Column(columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "report", columnDefinition = "TEXT")
    private String report;

    @Column(columnDefinition = "TEXT")
    private String reject_reason;

    @ManyToOne
    @JoinColumn(name = "document_id")
    @JsonIgnore
    private Document document;


    @ManyToOne
    @JoinColumn(name = "trainee_application_id")
    @JsonIgnore
    private TraineeApplication traineeApplication;

    @OneToMany(mappedBy = "traineeDocumentSubmission", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Extract_Data_Trainee_Document> extractDataTraineeDocumentList;
}
