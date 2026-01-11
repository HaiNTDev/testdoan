package com.example.Automated.Application.Mangament.serviceImplements;


import com.example.Automated.Application.Mangament.dto.request.BatchDTO;
import com.example.Automated.Application.Mangament.dto.response.BatchResponse;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.model.Batch;
import com.example.Automated.Application.Mangament.repositories.BatchRepository;
import com.example.Automated.Application.Mangament.serviceInterfaces.BatchServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

@Service
public class BatchServiceImplement implements BatchServiceInterface {
    @Autowired
    BatchRepository batchRepository;
    public ResponseEntity<ResponseObj> getAll(){
        try{
            List<Batch> batchList = batchRepository.findAll();
            if(batchList.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "List empty", null));
            }
            List<BatchResponse> batchResponses = new ArrayList<>();
            for(Batch batch : batchList){
                batchResponses.add(covertBatchResponse(batch));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "List batch", batchResponses));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null));
        }
    }

    public BatchResponse covertBatchResponse(Batch batch){
        BatchResponse batchResponse = new BatchResponse();
        batchResponse.setStartDate(batch.getStartDate());
        batchResponse.setEndDate(batch.getEndDate());
        batchResponse.setStatus(batchResponse.isStatus());
        return batchResponse;
    }



        public ResponseEntity<ResponseObj> createBatch(BatchDTO batchDTO) {
            LocalDateTime start = batchDTO.getStartDate();
            LocalDateTime end = batchDTO.getEndDate();
            LocalDateTime now = LocalDateTime.now();


            if (start.isBefore(now.plusWeeks(1))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObj("Fail", "Ngày bắt đầu phải cách ngày hiện tại ít nhất 7 ngày để chuẩn bị.", null));
            }


            if (end.isBefore(start) || end.isEqual(start)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObj("Fail", "Ngày kết thúc phải sau ngày bắt đầu.", null ));
            }

            int currentYear = start.getYear();
            long batchCount = batchRepository.countByYear(currentYear);
            if (batchCount >= 3) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObj("Fail", "Trong năm " + currentYear + " đã tạo đủ 3 đợt nộp. Không thể tạo thêm.", null));
            }


            boolean isOverlapping = batchRepository.existsOverlappingBatch(start, end);
            if (isOverlapping) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObj("Fail", "Thời gian đợt nộp bị trùng lặp với một đợt nộp khác đã tồn tại.", null));
            }

            Batch newBatch = new Batch();
            newBatch.setStartDate(start);
            newBatch.setEndDate(end);

            batchRepository.save(newBatch);

            return ResponseEntity.ok(new ResponseObj("Success", "Tạo đợt nộp thành công.", null));
        }


    public ResponseEntity<ResponseObj> updateBatch(Long id, BatchDTO batchDTO) {

        Batch existingBatch = batchRepository.findById(id)
                .orElse(null);
        if (existingBatch == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObj("Fail", "Không tìm thấy đợt nộp này.", null));
        }

        if(existingBatch.isActive()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObj("Fail", "Dot nop dang hoat dong khong the update.", null));

        }

        LocalDateTime start = batchDTO.getStartDate();
        LocalDateTime end = batchDTO.getEndDate();
        LocalDateTime now = LocalDateTime.now();

        if (start.isBefore(now.plusWeeks(1))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObj("Fail", "Ngày bắt đầu mới phải cách ngày hiện tại ít nhất 7 ngày.", null));
        }

        if (end.isBefore(start) || end.isEqual(start)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObj("Fail", "Ngày kết thúc phải sau ngày bắt đầu.", null));
        }


        boolean isOverlapping = batchRepository.existsOverlappingBatchExcludingId(start, end, id);
        if (isOverlapping) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObj("Fail", "Thời gian cập nhật bị trùng lặp với một đợt nộp khác.", null));
        }


        existingBatch.setStartDate(start);
        existingBatch.setEndDate(end);

        batchRepository.save(existingBatch);

        return ResponseEntity.ok(new ResponseObj("Success", "Cập nhật đợt nộp thành công.", null));
    }


    public ResponseEntity<ResponseObj> deleteBatch(Long id) {
        Batch existingBatch = batchRepository.findById(id)
                .orElse(null);
        if (existingBatch == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObj("Fail", "Không tìm thấy đợt nộp.", null));
        }


        batchRepository.delete(existingBatch);

        return ResponseEntity.ok(new ResponseObj("Success", "Đã xóa đợt nộp thành công.", null));
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void autoUpdateBatchStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<Batch> allBatches = batchRepository.findAll();

        for (Batch batch : allBatches) {
            LocalDateTime start = batch.getStartDate();
            LocalDateTime end = batch.getEndDate();

            if (now.isAfter(start) && now.isBefore(end)) {
                if (!batch.isActive()) {
                    batch.setActive(true);
                }
            } else {
                if (batch.isActive()) {
                    batch.setActive(false);
                }
            }
        }

        batchRepository.saveAll(allBatches);

        System.out.println("--- CronJob: Đã cập nhật trạng thái các đợt nộp lúc " + now + " ---");
    }
}
