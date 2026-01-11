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
public class TraineeApplication extends BaseEntity{
    @Enumerated(EnumType.STRING)
    private StatusEnum statusEnum;

    @ManyToOne
    @JoinColumn(name = "position_id")
    @JsonIgnore
    private Position position;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

    @OneToMany(mappedBy = "traineeApplication", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Trainee_Document_Submission> traineeDocumentSubmissionList;
}
