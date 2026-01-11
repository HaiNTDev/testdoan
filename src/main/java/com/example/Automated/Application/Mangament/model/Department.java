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
public class Department extends BaseEntity{

    private String departmentName;

    @Column(columnDefinition = "Text")
    private String departmentDescription;

    private String departmentImage;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Position> positionList;

    @OneToMany(mappedBy = "department", fetch =  FetchType.LAZY)
    @JsonIgnore
    private List<Account> accountList;
}
