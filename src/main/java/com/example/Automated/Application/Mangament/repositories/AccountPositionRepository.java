package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.model.AccountPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountPositionRepository extends JpaRepository<AccountPosition, Long> {
}
