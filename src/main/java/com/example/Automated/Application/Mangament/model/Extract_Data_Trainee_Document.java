package com.example.Automated.Application.Mangament.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Extract_Data_Trainee_Document extends BaseEntity{
    private String data_name;
    @Column(columnDefinition = "TEXT")
    private String data;

    @ManyToOne
    @JoinColumn(name = "trainee_submission_id")
    @JsonIgnore
    private Trainee_Document_Submission traineeDocumentSubmission;
}
