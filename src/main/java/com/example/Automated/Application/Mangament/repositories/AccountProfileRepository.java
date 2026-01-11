package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.model.AccountProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountProfileRepository extends JpaRepository<AccountProfile, Long> {
    Optional<AccountProfile> findByAccountId(Long accountId);
}
