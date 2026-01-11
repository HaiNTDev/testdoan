package com.example.Automated.Application.Mangament.Controller;

import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.serviceInterfaces.PositionServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/position")
public class PositionController {
    @Autowired
    PositionServiceInterface positionServiceInterface;

    @GetMapping("/getAllPossition")
//    @PreAuthorize("hasRole('HEAD_OF_DEPARTMENT')")
    public ResponseEntity<ResponseObj> getAllPosition(){
        return positionServiceInterface.getAllPosition();
    }

    @GetMapping("/getPositionById/{position_id}")
//    @PreAuthorize("hasRole('HEAD_OF_DEPARTMENT')")
    public ResponseEntity<ResponseObj> getPositionById(@PathVariable Long position_id){
        return positionServiceInterface.getPositionById(position_id);
    }


//    @PreAuthorize("hasRole('HEAD_OF_DEPARTMENT')")
    @PostMapping(value = "/createPosition", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObj> createPosition( @RequestPart(value = "departmentID", required = false) String departmentId,
                                                       @RequestPart(value = "positionName", required = false) String positionName,
                                                      @RequestPart(value = "positionDescription", required = false) String positionDescription,
                                                      @RequestPart(value = "positionImage", required = false) MultipartFile positionImage){
        try{
            Long departmentID = Long.parseLong(departmentId);
            return positionServiceInterface.createPosition(departmentID,positionName, positionDescription, positionImage);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }
//
//    @PreAuthorize("hasRole('HEAD_OF_DEPARTMENT')")

    @PutMapping(value = "updatePositionById/{position_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObj> updatePositionById(@PathVariable Long position_id,
                                                          @RequestPart(value = "departmentID", required = false) String departmentId,
                                                          @RequestPart(value = "positionName", required = false) String positionName,
                                                          @RequestPart(value = "positionDescription", required = false) String positionDescription,
                                                          @RequestPart(value = "positionImage", required = false) MultipartFile positionImage){
        try{
            Long departmentID = Long.parseLong(departmentId);
            return positionServiceInterface.updatePosition(position_id,departmentID, positionName, positionDescription, positionImage);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/deletePositionById/{position_id}")
    public ResponseEntity<ResponseObj> deletePositionById(@PathVariable Long position_id){
        return  positionServiceInterface.deletePositionById(position_id);
    }

}
