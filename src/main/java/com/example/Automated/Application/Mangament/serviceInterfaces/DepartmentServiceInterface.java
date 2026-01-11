package com.example.Automated.Application.Mangament.serviceInterfaces;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface DepartmentServiceInterface {

    ResponseEntity<ResponseObj> createDepartment(String departmentName, String departmentDescription, MultipartFile departmentImage);

    ResponseEntity<ResponseObj> updateDepartment(Long id, String departmentName, String departmentDescription, MultipartFile departmentImage, String oldDepartmentImageUrl);
    ResponseEntity<ResponseObj> getAllDepartments();

    ResponseEntity<ResponseObj> getDepartmentById(Long id);

    ResponseEntity<ResponseObj> deleteDepartment(Long id);
}
