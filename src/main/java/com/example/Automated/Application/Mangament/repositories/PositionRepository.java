package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.model.Account;
import com.example.Automated.Application.Mangament.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Account> findByPositionName (String positionName);

//    List<Position> findAllByActiveIsTrue();
    Optional<Position> findByPositionNameIgnoreCase(String positionName);
    boolean existsByPositionName(String positionName);
}
