package com.example.Automated.Application.Mangament.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Position extends BaseEntity{

    private String positionName;


    @Column(columnDefinition = "Text")
    private String positionDescription;

    private String positionImage;

    @OneToMany(mappedBy = "position", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AccountPosition> accountPositionList;

    @OneToMany(mappedBy = "position", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<InputDocumentMatrix> inputDocumentMatrices;


    @OneToMany(mappedBy = "position", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TraineeApplication> traineeAplicationList;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
