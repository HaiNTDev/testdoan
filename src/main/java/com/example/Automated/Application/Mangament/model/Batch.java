package com.example.Automated.Application.Mangament.model;


import com.example.Automated.Application.Mangament.enums.BatchEnum;
import com.example.Automated.Application.Mangament.enums.MatrixStatusEnum;
import com.google.api.client.util.DateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Data
public class Batch extends BaseEntity{
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
