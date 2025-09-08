package com.parser.core.report.service;

import com.parser.core.report.dto.ReportRequestDto;
import com.parser.core.report.dto.ReportResponseDto;

public interface ReportService {
    ReportResponseDto generateCsvReport(ReportRequestDto request);
} 