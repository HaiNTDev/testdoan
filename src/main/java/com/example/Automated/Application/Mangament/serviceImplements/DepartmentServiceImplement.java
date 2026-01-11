package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.dto.response.DepartmentResponse;
import com.example.Automated.Application.Mangament.exception.AppException;
import com.example.Automated.Application.Mangament.exception.ErrorCode;
import com.example.Automated.Application.Mangament.model.Department;
import com.example.Automated.Application.Mangament.repositories.DepartmentRepository;
import com.example.Automated.Application.Mangament.serviceInterfaces.DepartmentServiceInterface;
import com.example.Automated.Application.Mangament.serviceInterfaces.UploadServiceInterface;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImplement implements DepartmentServiceInterface {
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private UploadServiceInterface uploadServiceInterface;

    //    @Override
//    @Transactional
//    public ResponseEntity<ResponseObj> createDepartment(String departmentName, String departmentDescription, String departmentImageUrl) {
//        try {
//            Department department = new Department();
//            department.setDepartmentName(departmentName);
//            department.setDepartmentDescription(departmentDescription);
//            department.setDepartmentImage(departmentImageUrl != null ? departmentImageUrl : "default_image_url");
//            department.setActive(true);
//
//            department = departmentRepository.save(department);
//            DepartmentResponse response = mapToResponse(department);
//            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObj(HttpStatus.CREATED.toString(), "Department created successfully", response));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error creating department: " + e.getMessage(), null));
//        }
//    }
    @Override
    @Transactional
    public ResponseEntity<ResponseObj> createDepartment(String departmentName, String departmentDescription, MultipartFile departmentImage) {
        try {
            String departmentImageUrl = null;
            if (departmentImage != null && !departmentImage.isEmpty()) {
                departmentImageUrl = uploadServiceInterface.uploadFile(departmentImage).getBody();
            }

            Department department = new Department();
            department.setDepartmentName(departmentName);
            department.setDepartmentDescription(departmentDescription);
            department.setDepartmentImage(departmentImageUrl != null ? departmentImageUrl : "default_image_url");
            department.setActive(true);

            department = departmentRepository.save(department);
            DepartmentResponse response = mapToResponse(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObj(HttpStatus.CREATED.toString(), "Department created successfully", response));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error uploading department image: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error creating department: " + e.getMessage(), null));
        }
    }


    @Override
    public ResponseEntity<ResponseObj> getAllDepartments() {
        try {
            List<Department> departments = departmentRepository.findAll();
            if (departments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "List is empty", null));
            }
            List<DepartmentResponse> responses = departments.stream().filter(Department::isActive).map(this::mapToResponse).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "List of departments", responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Server error: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ResponseObj> getDepartmentById(Long id) {
        try {
            Department department = departmentRepository.findById(id).filter(Department::isActive).orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));
            DepartmentResponse response = mapToResponse(department);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Department found", response));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Server error: " + e.getMessage(), null));
        }
    }

    //    @Override
//    @Transactional
//    public ResponseEntity<ResponseObj> updateDepartment(Long id, String departmentName, String departmentDescription, String departmentImageUrl) {
//        try {
//            Department department = departmentRepository.findById(id)
//                    .filter(Department::isActive)
//                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));
//
//            if (departmentName != null) department.setDepartmentName(departmentName);
//            if (departmentDescription != null) department.setDepartmentDescription(departmentDescription);
//            if (departmentImageUrl != null) department.setDepartmentImage(departmentImageUrl);
//
//            department = departmentRepository.save(department);
//            DepartmentResponse response = mapToResponse(department);
//            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Department updated successfully", response));
//        } catch (AppException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error updating department: " + e.getMessage(), null));
//        }
//    }
    @Override
    @Transactional
    public ResponseEntity<ResponseObj> updateDepartment(Long id, String departmentName, String departmentDescription, MultipartFile departmentImage, String oldDepartmentImageUrl) {
        try {
            Department department = departmentRepository.findById(id).filter(Department::isActive).orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));

            if (departmentName != null) department.setDepartmentName(departmentName);
            if (departmentDescription != null) department.setDepartmentDescription(departmentDescription);

            if (departmentImage != null && !departmentImage.isEmpty()) {
                String departmentImageUrl = uploadServiceInterface.updateFile(departmentImage, oldDepartmentImageUrl).getBody();
                department.setDepartmentImage(departmentImageUrl);
            }

            department = departmentRepository.save(department);
            DepartmentResponse response = mapToResponse(department);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Department updated successfully", response));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error updating department image: " + e.getMessage(), null));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error updating department: " + e.getMessage(), null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObj> deleteDepartment(Long id) {
        try {
            Department department = departmentRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));
            department.setActive(false);
            departmentRepository.save(department);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Department deleted successfully", null));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error deleting department: " + e.getMessage(), null));
        }
    }

    private DepartmentResponse mapToResponse(Department department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getId());
        response.setDepartmentName(department.getDepartmentName());
        response.setDepartmentDescription(department.getDepartmentDescription());
        response.setDepartmentImage(department.getDepartmentImage());
        return response;
    }
}
