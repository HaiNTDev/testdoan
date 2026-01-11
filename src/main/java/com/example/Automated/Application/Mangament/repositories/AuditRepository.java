package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.model.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, Long> {
    boolean existsByUsernameAndApiMethodAndSuccess(String username, String apiMethod, boolean success);
    List<AuditEntity> findByUsername(String username);
}
