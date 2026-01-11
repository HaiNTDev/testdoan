package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findById(Long aLong);
    Optional<Department> findByDepartmentNameIgnoreCase(String departmentName);
}
