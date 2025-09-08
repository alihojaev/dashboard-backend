package com.parser.core.translate.service;

import com.parser.core.common.enums.Language;

import java.util.List;

public interface TranslateService {

    List<String> translateTexts(List<String> texts, Language targetLanguage);
}
