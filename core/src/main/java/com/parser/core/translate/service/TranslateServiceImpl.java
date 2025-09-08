package com.parser.core.translate.service;

import com.parser.core.common.enums.Language;
import com.parser.core.translate.dto.YandexTranslateRequest;
import com.parser.core.translate.dto.YandexTranslateResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TranslateServiceImpl implements TranslateService {

    private String apiKey;
    private String folderId;
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public TranslateServiceImpl(
            @Value("${yandex.translate.api-key}") String apiKey,
            @Value("${yandex.translate.folder-id}") String folderId,
            @Value("${yandex.translate.url}") String apiUrl) {
        this.apiKey = apiKey;
        this.folderId = folderId;
        this.apiUrl = apiUrl;
    }

    @Override
    public List<String> translateTexts(List<String> texts, Language targetLanguage) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Api-Key " + apiKey);

            // Prepare texts for translation
            List<String> preparedTexts = texts;
            Map<Integer, Map<String, String>> placeholders = new HashMap<>();

            if (targetLanguage == Language.kg) {
                preparedTexts = prepareTextsForKyrgyzTranslation(texts, placeholders);
                // Log prepared texts for debugging
                for (int i = 0; i < texts.size(); i++) {
                    if (!texts.get(i).equals(preparedTexts.get(i))) {
                        log.debug("Prepared text for Kyrgyz translation: '{}' -> '{}'",
                                texts.get(i), preparedTexts.get(i));
                    }
                }
            }

            // Create request body
            YandexTranslateRequest request = new YandexTranslateRequest(
                    folderId,
                    Language.ru.getCode(),
                    targetLanguage.getCode(),
                    preparedTexts
            );

            // Make API request
            HttpEntity<YandexTranslateRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<YandexTranslateResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    YandexTranslateResponse.class
            );

            // Extract translated texts from response
            if (response.getBody() != null && response.getBody().getTranslations() != null) {
                List<String> translatedTexts = response.getBody().getTranslations()
                        .stream()
                        .map(YandexTranslateResponse.Translation::getText)
                        .collect(Collectors.toList());

                // Clean up and restore placeholders for Kyrgyz translations
                if (targetLanguage == Language.kg) {
                    return cleanTranslatedTexts(translatedTexts, placeholders);
                }

                return translatedTexts;
            }

            log.warn("Translation response is null or empty for texts: {}", texts);
            return texts; // Return original texts if translation fails

        } catch (Exception e) {
            log.error("Error translating texts: {}", texts, e);
            return texts; // Return original texts if translation fails
        }
    }

    /**
     * Prepares texts for Kyrgyz translation by extracting only translatable parts
     */
    private List<String> prepareTextsForKyrgyzTranslation(List<String> texts, Map<Integer, Map<String, String>> placeholders) {
        List<String> preparedTexts = new ArrayList<>();

        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            if (text == null) {
                preparedTexts.add(null);
                continue;
            }

            Map<String, String> textPlaceholders = new HashMap<>();
            String prepared = extractTranslatableParts(text, textPlaceholders);
            preparedTexts.add(prepared);
            placeholders.put(i, textPlaceholders);
        }

        return preparedTexts;
    }

    /**
     * Extracts only the parts that should be translated to Kyrgyz, preserving model codes and special characters
     */
    private String extractTranslatableParts(String text, Map<String, String> placeholders) {
        // Pattern to match model codes like NA64H3010AS/WT, NA64H3010AK/WT
        Pattern modelCodePattern = Pattern.compile("([A-Z]{2}\\d+[A-Z]+\\d+[A-Z]+\\/[A-Z]+)");

        // Pattern to match technical specifications like 5G, 4K UHD, 120 Гц, A-10%, 120кадр/с @HD
        Pattern techSpecPattern = Pattern.compile("(\\d+[A-Z]+|[A-Z]+\\d+|[A-Z]-\\d+%|\\d+кадр\\/с\\s*@[A-Z]+|\\d+\\s*Гц|\\d+\\s*ГЦ|\\d+\\s*кадр\\/с)");

        // Pattern to match common technical terms that should not be translated
        Pattern techTermsPattern = Pattern.compile("(AMOLED|Dynamic|All-Around|Cooling|UHD|HD|Плавный)");

        // Pattern to match numbers and units
        Pattern numbersPattern = Pattern.compile("(\\d+%|\\d+\\s*Гц|\\d+\\s*ГЦ|\\d+кадр\\/с)");

        // Pattern to match special characters and formatting that should be preserved
        Pattern specialCharsPattern = Pattern.compile("([@\\/\\-\\+\\=\\s]+)");

        // Replace model codes with placeholders
        Matcher modelMatcher = modelCodePattern.matcher(text);
        String result = text;
        int placeholderIndex = 0;

        while (modelMatcher.find()) {
            String modelCode = modelMatcher.group(1);
            String placeholder = "MODEL_" + placeholderIndex++;
            placeholders.put(placeholder, modelCode);
            result = result.replace(modelCode, placeholder);
        }

        // Replace technical specifications with placeholders
        Matcher techMatcher = techSpecPattern.matcher(result);
        while (techMatcher.find()) {
            String techSpec = techMatcher.group(1);
            String placeholder = "TECH_" + placeholderIndex++;
            placeholders.put(placeholder, techSpec);
            result = result.replace(techSpec, placeholder);
        }

        // Replace technical terms with placeholders
        Matcher termsMatcher = techTermsPattern.matcher(result);
        while (termsMatcher.find()) {
            String term = termsMatcher.group(1);
            String placeholder = "TERM_" + placeholderIndex++;
            placeholders.put(placeholder, term);
            result = result.replace(term, placeholder);
        }

        // Replace numbers and units with placeholders
        Matcher numbersMatcher = numbersPattern.matcher(result);
        while (numbersMatcher.find()) {
            String number = numbersMatcher.group(1);
            String placeholder = "NUM_" + placeholderIndex++;
            placeholders.put(placeholder, number);
            result = result.replace(number, placeholder);
        }

        // Replace special characters with placeholders to preserve formatting
        Matcher specialMatcher = specialCharsPattern.matcher(result);
        while (specialMatcher.find()) {
            String specialChar = specialMatcher.group(1);
            String placeholder = "SPECIAL_" + placeholderIndex++;
            placeholders.put(placeholder, specialChar);
            result = result.replace(specialChar, placeholder);
        }

        // Additional processing for specific cases
        result = processSpecificCases(result, placeholders, placeholderIndex);

        return result;
    }

    /**
     * Processes specific cases that need special handling
     */
    private String processSpecificCases(String text, Map<String, String> placeholders, int startIndex) {
        String result = text;
        int placeholderIndex = startIndex;

        // Handle specific model code patterns that might be missed
        Pattern specificModelPattern = Pattern.compile("([A-Z]{2}\\d+[A-Z]+\\d+[A-Z]+)");
        Matcher specificModelMatcher = specificModelPattern.matcher(result);
        while (specificModelMatcher.find()) {
            String modelCode = specificModelMatcher.group(1);
            if (!placeholders.containsValue(modelCode)) {
                String placeholder = "SPECIFIC_MODEL_" + placeholderIndex++;
                placeholders.put(placeholder, modelCode);
                result = result.replace(modelCode, placeholder);
            }
        }

        // Handle energy efficiency class patterns like A-10%
        Pattern energyClassPattern = Pattern.compile("([A-Z]-\\d+%)");
        Matcher energyClassMatcher = energyClassPattern.matcher(result);
        while (energyClassMatcher.find()) {
            String energyClass = energyClassMatcher.group(1);
            String placeholder = "ENERGY_" + placeholderIndex++;
            placeholders.put(placeholder, energyClass);
            result = result.replace(energyClass, placeholder);
        }

        return result;
    }

    /**
     * Cleans translated texts and restores original placeholders
     */
    private List<String> cleanTranslatedTexts(List<String> translatedTexts, Map<Integer, Map<String, String>> placeholders) {
        List<String> cleanedTexts = new ArrayList<>();

        for (int i = 0; i < translatedTexts.size(); i++) {
            String translatedText = translatedTexts.get(i);
            if (translatedText == null) {
                cleanedTexts.add(null);
                continue;
            }

            Map<String, String> textPlaceholders = placeholders.get(i);
            if (textPlaceholders != null) {
                String cleaned = restorePlaceholders(translatedText, textPlaceholders);
                cleanedTexts.add(cleaned);
            } else {
                cleanedTexts.add(translatedText);
            }
        }

        return cleanedTexts;
    }

    /**
     * Restores original values from placeholders and fixes common translation errors
     */
    private String restorePlaceholders(String text, Map<String, String> placeholders) {
        String result = text;

        // Restore placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = entry.getKey();
            String originalValue = entry.getValue();
            result = result.replace(placeholder, originalValue);
        }

        return result;
    }
}