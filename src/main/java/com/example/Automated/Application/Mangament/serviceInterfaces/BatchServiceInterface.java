package com.example.Automated.Application.Mangament.serviceInterfaces;

import com.example.Automated.Application.Mangament.dto.request.BatchDTO;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public interface BatchServiceInterface {
    public ResponseEntity<ResponseObj> getAll();
    public ResponseEntity<ResponseObj> createBatch(BatchDTO batchDTO);
    public ResponseEntity<ResponseObj> updateBatch(Long id, BatchDTO batchDTO);
    public ResponseEntity<ResponseObj> deleteBatch(Long id);
}
