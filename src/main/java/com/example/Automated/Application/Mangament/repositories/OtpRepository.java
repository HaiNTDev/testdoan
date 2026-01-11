package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.model.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity> findByEmailAndOtpAndUsedFalse(String email, String otp);
    void deleteByEmail(String email);
}
