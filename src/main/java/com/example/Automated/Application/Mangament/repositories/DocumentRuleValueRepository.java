package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.model.DocumentRuleValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRuleValueRepository extends JpaRepository<DocumentRuleValue, Long> {
}
