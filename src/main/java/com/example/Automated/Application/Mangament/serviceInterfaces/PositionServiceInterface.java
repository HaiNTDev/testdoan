package com.example.Automated.Application.Mangament.serviceInterfaces;


import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface PositionServiceInterface {
    ResponseEntity<ResponseObj> createPosition(Long departmentId, String positionName, String positionDescription, MultipartFile positionImage);

    ResponseEntity<ResponseObj> updatePosition(Long id,Long departmentId, String positionName, String positionDescription, MultipartFile positionImage);

    ResponseEntity<ResponseObj> getAllPosition();

    ResponseEntity<ResponseObj> getPositionById(Long id);

    ResponseEntity<ResponseObj> deletePositionById(Long id);
}
