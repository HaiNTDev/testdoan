package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.model.Document;
import com.example.Automated.Application.Mangament.model.InputDocumentMatrix;
import com.example.Automated.Application.Mangament.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface InputDocumentMatrixRepository extends JpaRepository<InputDocumentMatrix, Long> {

//    List<InputDocumentMatrix> findAllWithPositionAndDepartment();

    List<InputDocumentMatrix> findByPosition(Position position);

    List<InputDocumentMatrix> findByDocument(Document document);


    Optional<InputDocumentMatrix> findByPositionIdAndDocumentId(long positionId, long documentId);


    Optional<InputDocumentMatrix> findByDocumentIdAndPositionId(Long documentId, Long positionId);

    @Query("SELECT m FROM InputDocumentMatrix m " +
            "JOIN m.position p " +
            "JOIN p.department d " +
            "WHERE (:departmentId IS NULL OR d.id = :departmentId) " +
            "AND (:positionId IS NULL OR p.id = :positionId)")
    List<InputDocumentMatrix> filterMatrix(@Param("departmentId") Long departmentId,
                                           @Param("positionId") Long positionId);

    @Query("SELECT m FROM InputDocumentMatrix m " +
            "JOIN FETCH m.position p " +
            "JOIN FETCH p.department d " +
            "JOIN FETCH m.document doc " +
            "LEFT JOIN FETCH m.documentRuleValueList rv " +
            "LEFT JOIN FETCH rv.documentRule dr " +
            "WHERE m.id IN :ids")
    List<InputDocumentMatrix> findAllDetailsByIds(@Param("ids") List<Long> ids);
}
