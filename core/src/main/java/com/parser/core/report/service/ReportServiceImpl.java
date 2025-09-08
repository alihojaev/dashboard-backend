package com.parser.core.report.service;

import com.parser.core.exceptions.BadRequestException;
import com.parser.core.report.dto.ReportRequestDto;
import com.parser.core.report.dto.ReportResponseDto;
import com.parser.core.report.util.CsvUtil;
import com.parser.core.report.util.RepositoryUtil;
import com.parser.core.report.util.ReflectionUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReportServiceImpl implements ReportService {
    ApplicationContext applicationContext;

    @Override
    public ReportResponseDto generateCsvReport(ReportRequestDto request) {
        long startTime = System.currentTimeMillis();
        try {
            validateRequest(request);
            JpaRepository<?, ?> repository = RepositoryUtil.getRepository(applicationContext, request.getEntityName());
            List<?> data;
            if (request.getRsqlFilter() != null && !request.getRsqlFilter().trim().isEmpty()) {
                data = RepositoryUtil.getDataWithRsqlFilter(repository, request.getRsqlFilter());
            } else {
                data = RepositoryUtil.getAllData(repository);
            }
            List<String> headers = prepareHeaders(request);
            List<List<Object>> dataRows = prepareDataRows(data, request.getFields());
            String csvData = CsvUtil.createCsvDocumentWithBom(headers, dataRows);
            String fileName = generateFileName(request.getEntityName());
            long generationTime = System.currentTimeMillis() - startTime;
            return new ReportResponseDto(
                    csvData,
                    fileName,
                    (long) data.size(),
                    generationTime
            );
        } catch (Exception e) {
            log.error("Ошибка при генерации отчета: {}", e.getMessage(), e);
            throw new BadRequestException("Ошибка при генерации отчета: " + e.getMessage());
        }
    }

    private void validateRequest(ReportRequestDto request) {
        if (request.getEntityName() == null || request.getEntityName().trim().isEmpty()) {
            throw new BadRequestException("Название entity не может быть пустым");
        }
        if (request.getFields() == null || request.getFields().isEmpty()) {
            throw new BadRequestException("Список полей не может быть пустым");
        }
        if (request.getHeaders() == null) {
            request.setHeaders(new HashMap<>());
        }
    }

    private List<String> prepareHeaders(ReportRequestDto request) {
        return request.getFields().stream()
                .map(field -> request.getHeaders().getOrDefault(field, field))
                .collect(Collectors.toList());
    }

    private List<List<Object>> prepareDataRows(List<?> data, List<String> fields) {
        return data.stream()
                .map(item -> fields.stream().map(field -> ReflectionUtil.getFieldValue(item, field)).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private String generateFileName(String entityName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("report_%s_%s.csv", entityName.toLowerCase(), timestamp);
    }
} 