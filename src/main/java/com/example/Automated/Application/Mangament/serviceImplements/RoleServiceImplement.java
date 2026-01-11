package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.model.Role;
import com.example.Automated.Application.Mangament.repositories.RoleRepository;
import com.example.Automated.Application.Mangament.dto.request.RoleDTO;
import com.example.Automated.Application.Mangament.serviceInterfaces.RoleServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RoleServiceImplement implements RoleServiceInterface {
    @Autowired
    RoleRepository roleRepository;
    @Override
    public ResponseEntity<ResponseObj> createRole(RoleDTO roleDTO) {
        try{
            if(roleRepository.findByRoleName(roleDTO.getRoleName()).isPresent()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObj(HttpStatus.CONFLICT.toString(), "Role name has exist", roleDTO.getRoleName()));
            }
            Role role = new Role();
            role.setRoleName(roleDTO.getRoleName());
            role.setActive(true);
            role.setCreateAt(LocalDateTime.now());
            roleRepository.save(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObj(HttpStatus.CREATED.toString(), "Create role successfully", role));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error creating role: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ResponseObj> getAllRole() {
        try{
            var role = roleRepository.findAll();
            if(role.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "List is empty", role));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "List role!!", role));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage().toString(), null));
        }
    }
}
