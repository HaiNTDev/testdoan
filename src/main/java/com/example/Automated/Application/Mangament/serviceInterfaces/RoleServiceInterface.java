package com.example.Automated.Application.Mangament.serviceInterfaces;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.dto.request.RoleDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public interface RoleServiceInterface {
    public ResponseEntity<ResponseObj> createRole(RoleDTO roleDTO);

    public ResponseEntity<ResponseObj> getAllRole();
}
