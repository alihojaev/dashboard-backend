package com.parser.core.translate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YandexTranslateRequest {
    private String folderId;
    private String sourceLanguageCode;
    private String targetLanguageCode;
    private List<String> texts;
} 