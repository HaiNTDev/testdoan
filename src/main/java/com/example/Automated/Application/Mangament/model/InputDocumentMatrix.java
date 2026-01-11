    package com.example.Automated.Application.Mangament.model;

    import com.example.Automated.Application.Mangament.enums.MatrixStatusEnum;
    import com.example.Automated.Application.Mangament.enums.StatusEnum;
    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.time.LocalDateTime;
    import java.util.List;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Entity
    public class InputDocumentMatrix extends BaseEntity{
        @Enumerated(EnumType.STRING)
        private StatusEnum statusEnum;

        @Enumerated(EnumType.STRING)
        private MatrixStatusEnum matrixStatusEnum;


        private boolean required;

        private LocalDateTime startDate_deadLine;

        private LocalDateTime endDate_deadLine;

        private String rejection_reason;

        @ManyToOne
        @JoinColumn(name = "position_id")
        @JsonIgnore
        private Position position;

        @ManyToOne
        @JoinColumn(name = "document_id")
        @JsonIgnore
        private Document document;

        @OneToMany(mappedBy = "inputDocumentMatrix", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
        @JsonIgnore
        private List<DocumentRuleValue> documentRuleValueList;

        @OneToMany(mappedBy = "inputDocumentMatrix", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
        @JsonIgnore
        private List<MatrixExpiration> matrixExpirations;
    }
