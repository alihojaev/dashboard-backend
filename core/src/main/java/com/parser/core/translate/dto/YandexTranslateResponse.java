package com.parser.core.translate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YandexTranslateResponse {
    private List<Translation> translations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Translation {
        private String text;
    }
} 