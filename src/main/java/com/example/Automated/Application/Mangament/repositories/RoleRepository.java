package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.enums.RoleEnum;
import com.example.Automated.Application.Mangament.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleEnum roleName);

}
