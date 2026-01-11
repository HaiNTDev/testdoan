package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.model.Department;
import com.example.Automated.Application.Mangament.model.Position;
import com.example.Automated.Application.Mangament.repositories.DepartmentRepository;
import com.example.Automated.Application.Mangament.repositories.PositionRepository;
import com.example.Automated.Application.Mangament.serviceInterfaces.PositionServiceInterface;
import com.example.Automated.Application.Mangament.serviceInterfaces.UploadImageFileInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PositionServiceImplement implements PositionServiceInterface {
    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    UploadImageFileInterface uploadImageFileInterface;

    @Override
    public ResponseEntity<ResponseObj> createPosition(Long departmentId,String positionName, String positionDescription, MultipartFile positionImage) {
       try{
           Optional<Department> department = departmentRepository.findById(departmentId);
           if(!department.isPresent()){
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), "Department does not exist", null));
           }
           Position position = new Position();
           if (positionName == null || positionName.isBlank()) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                       new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Position name must not be null or blank.", null)
               );
           }

           if (positionRepository.existsByPositionName(positionName)) {
               return ResponseEntity.status(HttpStatus.CONFLICT).body(
                       new ResponseObj(HttpStatus.CONFLICT.toString(), "This position name already exists.", null)
               );
           }

           if (positionDescription == null || positionDescription.isBlank()) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                       new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Position description must not be null or blank.", null)
               );
           }

           if (positionImage == null || positionImage.isEmpty()) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                       new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Position image file must not be null or empty.", null)
               );
           }

           position.setPositionName(positionName);
           position.setPositionDescription(positionDescription);
           position.setDepartment(department.get());
           try {
               position.setPositionImage(uploadImageFileInterface.uploadImage(positionImage));
           } catch (IOException e) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
           }
           position.setCreateAt(LocalDateTime.now());
           position.setActive(true);
           positionRepository.save(position);
           return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObj(HttpStatus.CREATED.toString(), "Position create successfully", position));
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
       }
    }

    @Override
    public ResponseEntity<ResponseObj> updatePosition(Long id,Long departmentId, String positionName, String positionDescription, MultipartFile positionImage) {
       try{
           Optional<Department> department = departmentRepository.findById(departmentId);
           if(!department.isPresent()){
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), "Department does not exist", null));
           }
           Optional<Position> existingPosition = positionRepository.findById(id);
           if(!existingPosition.isPresent()){
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(),"Position Not found", null));
           }

           if (positionName.isBlank()) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                       new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Position name must not be null or blank.", null)
               );
           }

           if (positionRepository.existsByPositionName(positionName)) {
               return ResponseEntity.status(HttpStatus.CONFLICT).body(
                       new ResponseObj(HttpStatus.CONFLICT.toString(), "This position name already exists.", null)
               );
           }

           if (positionDescription.isBlank()) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                       new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Position description must not be null or blank.", null)
               );
           }
           if(positionImage != null){
               try {
                   existingPosition.get().setPositionImage(uploadImageFileInterface.updateImage(positionImage, existingPosition.get().getPositionImage()));
               } catch (IOException e) {
                   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
               }
           }

           existingPosition.get().setDepartment(department.get());
           existingPosition.get().setPositionName(positionName);
           existingPosition.get().setUpdateAt(LocalDateTime.now());
           existingPosition.get().setPositionDescription(positionDescription);
           positionRepository.save(existingPosition.get());
       return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Udapte position succesfully", existingPosition.get()));
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
       }

    }

    @Override
    public ResponseEntity<ResponseObj> getAllPosition() {
        try{
            List<Position> positionList = new ArrayList<>();
            for(Position position : positionRepository.findAll()){
                if(position.isActive()){
                    positionList.add(position);
                }
            }
            if(positionList.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Position List is empty", positionList));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Position List: ", positionList));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ResponseObj> getPositionById(Long id) {
          try{
              Optional<Position> position = positionRepository.findById(id);
              if(!position.isPresent()){
                  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), "This position does not exist", null));
              }
              return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Position: ", position.get()));

          } catch (Exception e) {
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
          }
    }

    @Override
    public ResponseEntity<ResponseObj> deletePositionById(Long id) {
        try{
            Optional<Position> existingPosition = positionRepository.findById(id);
            if(!existingPosition.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObj(HttpStatus.NOT_FOUND.toString(), "This position does not exist", null));
            }
            existingPosition.get().setActive(false);
            existingPosition.get().setDeleteAt(LocalDateTime.now());
            positionRepository.save(existingPosition.get());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Delete position successfully: ", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }
}
