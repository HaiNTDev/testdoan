package com.example.Automated.Application.Mangament.controller;



import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.serviceInterfaces.UploadServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/uploads")
public class UploadController {

    @Autowired
    private UploadServiceInterface uploadServiceInterface;

    @Operation(summary = "Upload a file", description = "Uploads a file to Cloudinary and returns the URL")
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> uploadFile(@Parameter(description = "File to upload", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
                                                      @RequestParam("file") MultipartFile file) {

        try {
            String url = uploadServiceInterface.uploadFile(file).getBody();
            return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "File uploaded successfully", url));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error uploading file: " + e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @Operation(summary = "Update a file", description = "Updates a file on Cloudinary by replacing the old one and returns the new URL")
    @PutMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> updateFile(@Parameter(description = "File to upload", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
                                                      @RequestParam("file") MultipartFile file,
                                                  @RequestParam("oldPublicId") String oldPublicId) {
        try {
            String url = uploadServiceInterface.updateFile(file, oldPublicId).getBody();
            return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "File updated successfully", url));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error updating file: " + e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }
//
//    @DeleteMapping("/file")
////    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ResponseObj> deleteFile(@RequestParam("publicId") String publicId) {
//        try {
//            uploadServiceInterface.deleteFile(publicId);
//            return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "File deleted successfully", null));
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error deleting file: " + e.getMessage(), null));
//        }
//    }
}