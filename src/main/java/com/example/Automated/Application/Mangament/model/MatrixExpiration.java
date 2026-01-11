package com.example.Automated.Application.Mangament.model;

import com.google.api.client.util.DateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Data
public class MatrixExpiration extends BaseEntity{
    private DateTime startDate;
    private DateTime endDate;

    @ManyToOne
    @JoinColumn(name = "matrix_id")
    private InputDocumentMatrix inputDocumentMatrix;
}
