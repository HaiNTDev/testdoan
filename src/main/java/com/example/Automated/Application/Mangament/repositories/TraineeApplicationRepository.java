package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.example.Automated.Application.Mangament.model.Account;
import com.example.Automated.Application.Mangament.model.TraineeApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TraineeApplicationRepository extends JpaRepository<TraineeApplication, Long> {
    boolean existsByAccount(Account account);

    List<TraineeApplication> findByStatusEnumAndIsActive(StatusEnum statusEnum, Boolean isActive);

    List<TraineeApplication> findByStatusEnum(StatusEnum status);

    List<TraineeApplication> findByPosition_Id(long positionId);

    TraineeApplication findFirstByAccountIdOrderByCreateAtDesc(Long accountId);

    @Query("SELECT " +
            "COUNT(ta), " +
            "SUM(CASE WHEN ta.statusEnum = 'InProgress' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN ta.statusEnum = 'Reject' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN ta.statusEnum = 'Approve' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN ta.statusEnum = 'Complete' THEN 1 ELSE 0 END) " +
            "FROM TraineeApplication ta")
    Object getStaffGlobalStats();
}
