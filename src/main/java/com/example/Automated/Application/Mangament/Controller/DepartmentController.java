package com.example.Automated.Application.Mangament.Controller;


import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.serviceInterfaces.DepartmentServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/departments")
public class DepartmentController {
    @Autowired
    private DepartmentServiceInterface departmentServiceInterface;

//    @Autowired
//    private UploadServiceInterface uploadServiceInterface;
//
//    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
////    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ResponseObj> createDepartment(
//            @RequestPart(value = "departmentName", required = true) String departmentName,
//            @RequestPart(value = "departmentDescription", required = false) String departmentDescription,
//            @RequestPart(value = "departmentImage", required = false) MultipartFile departmentImage) {
//        String departmentImageUrl = null;
//        if (departmentImage != null && !departmentImage.isEmpty()) {
//            try {
//                departmentImageUrl = uploadServiceInterface.uploadFile(departmentImage).getBody();
//            } catch (IOException e) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error uploading department image: " + e.getMessage(), null));
//            }
//        }
//
//        return departmentServiceInterface.createDepartment(departmentName, departmentDescription, departmentImageUrl);
//    }
@PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    @PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ResponseObj> createDepartment(
        @RequestPart(value = "departmentName", required = true) String departmentName,
        @RequestPart(value = "departmentDescription", required = false) String departmentDescription,
        @RequestPart(value = "departmentImage", required = false) MultipartFile departmentImage) {
    return departmentServiceInterface.createDepartment(departmentName, departmentDescription, departmentImage);
}



    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> getAllDepartments() {
        return departmentServiceInterface.getAllDepartments();
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> getDepartmentById(@PathVariable Long id) {
        return departmentServiceInterface.getDepartmentById(id);
    }

//    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
////    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ResponseObj> updateDepartment(
//            @PathVariable Long id,
//            @RequestPart(value = "departmentName", required = false) String departmentName,
//            @RequestPart(value = "departmentDescription", required = false) String departmentDescription,
//            @RequestPart(value = "departmentImage", required = false) MultipartFile departmentImage,
//            @RequestPart(value = "oldDepartmentImageUrl", required = false) String oldDepartmentImageUrl) {
//        String departmentImageUrl = null;
//        if (departmentImage != null && !departmentImage.isEmpty()) {
//            try {
//                departmentImageUrl = uploadServiceInterface.updateFile(departmentImage, oldDepartmentImageUrl).getBody();
//            } catch (IOException e) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error updating department image: " + e.getMessage(), null));
//            }
//        }
//
//        return departmentServiceInterface.updateDepartment(id, departmentName, departmentDescription, departmentImageUrl);
//    }
@PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    @PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ResponseObj> updateDepartment(
        @PathVariable Long id,
        @RequestPart(value = "departmentName", required = false) String departmentName,
        @RequestPart(value = "departmentDescription", required = false) String departmentDescription,
        @RequestPart(value = "departmentImage", required = false) MultipartFile departmentImage,
        @RequestPart(value = "oldDepartmentImageUrl", required = false) String oldDepartmentImageUrl) {
    return departmentServiceInterface.updateDepartment(id, departmentName, departmentDescription, departmentImage, oldDepartmentImageUrl);
}


    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> deleteDepartment(@PathVariable Long id) {
        return departmentServiceInterface.deleteDepartment(id);
    }
}
