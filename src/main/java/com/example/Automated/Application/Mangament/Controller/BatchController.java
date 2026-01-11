package com.example.Automated.Application.Mangament.Controller;

import com.example.Automated.Application.Mangament.dto.request.BatchDTO;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.serviceInterfaces.BatchServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batch")
public class BatchController {
    @Autowired
    private BatchServiceInterface batchService;

    @GetMapping
    public ResponseEntity<ResponseObj> getAllBatches() {
        return batchService.getAll();
    }

    @PostMapping("/create-batch")
    public ResponseEntity<ResponseObj> createBatch(@RequestBody BatchDTO batchDTO) {
        return batchService.createBatch(batchDTO);
    }

    @PutMapping("update-batch/{id}")
    public ResponseEntity<ResponseObj> updateBatch(@PathVariable Long id, @RequestBody BatchDTO batchDTO) {
        return batchService.updateBatch(id, batchDTO);
    }

    @DeleteMapping("delete-batch/{id}")
    public ResponseEntity<ResponseObj> deleteBatch(@PathVariable Long id) {
        return batchService.deleteBatch(id);
    }
}
