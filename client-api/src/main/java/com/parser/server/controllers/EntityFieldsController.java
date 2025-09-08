package com.parser.server.controllers;

import com.parser.core.entityFields.dto.EntityFieldInfoDto;
import com.parser.core.entityFields.service.EntityFieldsService;
import com.parser.core.report.dto.ReportRequestDto;
import com.parser.core.report.dto.ReportResponseDto;
import com.parser.core.report.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/entityFields")
@Slf4j
public class EntityFieldsController {
    
    @Autowired
    private EntityFieldsService entityFieldsService;
    @Autowired
    private ReportService reportService;
    
    @GetMapping("/entities")
    public ResponseEntity<List<String>> getAvailableEntities() {
        log.info("Получение списка доступных сущностей");
        List<String> entities = entityFieldsService.getAvailableEntities();
        return ResponseEntity.ok(entities);
    }
    
    @GetMapping("/entities/{entityName}/fields")
    public ResponseEntity<List<EntityFieldInfoDto>> getEntityFields(@PathVariable String entityName) {
        log.info("Получение полей для сущности: {}", entityName);
        List<EntityFieldInfoDto> fields = entityFieldsService.getEntityFields(entityName);
        return ResponseEntity.ok(fields);
    }

    @PostMapping("/generate")
    public ResponseEntity<ReportResponseDto> generateReport(@RequestBody ReportRequestDto request) {
        ReportResponseDto response = reportService.generateCsvReport(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadReport(@RequestBody ReportRequestDto request) {
        ReportResponseDto response = reportService.generateCsvReport(request);
        byte[] csvBytes = response.getCsvData().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", response.getFileName());
        headers.setContentLength(csvBytes.length);
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
} 