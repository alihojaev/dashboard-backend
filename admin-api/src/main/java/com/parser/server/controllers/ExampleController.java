package com.parser.server.controllers;

import com.parser.core.auth.role.enums.AccessType;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.config.permission.annotation.HasAccess;
import com.parser.core.config.permission.annotation.HasPermission;
import com.parser.core.entityFields.dto.EntityFieldInfoDto;
import com.parser.core.entityFields.service.EntityFieldsService;
import com.parser.core.example.dto.ExampleDictDto;
import com.parser.core.example.entity.ExampleDictEntity;
import com.parser.core.example.service.ExampleDictService;
import com.parser.core.report.dto.ReportRequestDto;
import com.parser.core.report.dto.ReportResponseDto;
import com.parser.core.report.service.ReportService;
import com.parser.core.util.rsql.RsqlSpecificationFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/example")
@HasPermission(PermissionType.EXAMPLE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExampleController {

    ExampleDictService service;
    EntityFieldsService entityFieldsService;
    ReportService reportService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @HasAccess(AccessType.READ)
    public ResponseEntity<?> listAll(String query, Pageable pageable) {
        RsqlSpecificationFactory<ExampleDictEntity> factory = new RsqlSpecificationFactory<>();
        Specification<ExampleDictEntity> spec = factory.createSpecification(query);
        return ResponseEntity.ok(service.searchPageable(spec, pageable));
    }

    @GetMapping("/fields")
    public List<EntityFieldInfoDto> getFields() {
        return entityFieldsService.getEntityFields("ExampleDictEntity");
    }

    @GetMapping("/{id}")
    public ExampleDictDto getById(@PathVariable("id") UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @HasAccess(AccessType.CREATE)
    public UUID save(@RequestBody ExampleDictDto dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public UUID update(@PathVariable("id") UUID id, @RequestBody ExampleDictDto dto) {
        return service.update(id, dto);
    }

    @PostMapping("/generate")
    @HasAccess(AccessType.READ)
    public ResponseEntity<ReportResponseDto> generateReport(@RequestBody ReportRequestDto request) {
        ReportResponseDto response = reportService.generateCsvReport(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/download")
    @HasAccess(AccessType.READ)
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

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id) {
        service.delete(id);
    }
}
