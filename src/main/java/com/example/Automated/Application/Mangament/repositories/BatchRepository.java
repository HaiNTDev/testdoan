package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    @Query("SELECT COUNT(b) FROM Batch b WHERE YEAR(b.startDate) = :year")
    long countByYear(@Param("year") int year);

    @Query("SELECT COUNT(b) > 0 FROM Batch b WHERE " +
            "( :start BETWEEN b.startDate AND b.endDate ) OR " +
            "( :end BETWEEN b.startDate AND b.endDate ) OR " +
            "( b.startDate BETWEEN :start AND :end )")
    boolean existsOverlappingBatch(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b) > 0 FROM Batch b WHERE b.id <> :id AND (" +
            "( :start BETWEEN b.startDate AND b.endDate ) OR " +
            "( :end BETWEEN b.startDate AND b.endDate ) OR " +
            "( b.startDate BETWEEN :start AND :end ))")
    boolean existsOverlappingBatchExcludingId(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end,
                                              @Param("id") Long id);
}
