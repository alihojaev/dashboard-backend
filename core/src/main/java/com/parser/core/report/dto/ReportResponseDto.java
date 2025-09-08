package com.parser.core.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDto {
    private String csvData;
    private String fileName;
    private Long rowCount;
    private Long generationTimeMs;
} 