package com.parser.core.client.service;

import com.parser.core.client.entity.ClientEntity;
import com.parser.core.client.repo.ClientRepo;
import com.parser.core.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientAuthService {
    
    private final ClientRepo clientRepo;
    private final ClientTokenService clientTokenService;
    private final Gson gson = new Gson();
    
    /**
     * Находит клиента по токену
     * Поддерживает различные форматы токенов:
     * - Client JWT token (бессрочный токен для клиентов)
     * - Google JWT token (извлекает sub claim)
     * - google-token-{timestamp} (временный формат для фронтенда)
     * - google-token-{googleId}
     * - email-token-{userId}
     */
    public Optional<ClientEntity> findClientByToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            // Сначала пробуем обработать как клиентский JWT токен (бессрочный)
            if (clientTokenService.isClientToken(token) && clientTokenService.verifyClientToken(token)) {
                UUID clientId = clientTokenService.getClientId(token);
                if (clientId != null) {
                    log.debug("Found client by JWT token: {}", clientId);
                    return clientRepo.findById(clientId);
                }
            }
            
            // Затем пробуем обработать как Google JWT токен
            if (isGoogleJwtToken(token)) {
                String googleId = extractGoogleIdFromJwt(token);
                if (googleId != null) {
                    log.debug("Extracted Google ID from JWT: {}", googleId);
                    return clientRepo.findByGoogleId(googleId);
                }
            }
            
            // Обработка других форматов токенов
            if (token.startsWith("google-token-")) {
                String suffix = token.substring("google-token-".length());
                
                // Проверяем, является ли суффикс Google ID (длинная строка)
                if (suffix.length() > 20) {
                    // Это Google ID
                    return clientRepo.findByGoogleId(suffix);
                } else {
                    // Это timestamp - ищем пользователя по email из последней сессии
                    // Для временного решения возвращаем первого пользователя с Google ID
                    log.warn("Timestamp-based token detected: {}. This is a temporary solution.", token);
                    return findClientByTimestampToken(suffix);
                }
            } else if (token.startsWith("email-token-")) {
                String userIdStr = token.substring("email-token-".length());
                UUID userId = UUID.fromString(userIdStr);
                return clientRepo.findById(userId);
            } else {
                // Попробуем найти по ID напрямую (для обратной совместимости)
                try {
                    UUID userId = UUID.fromString(token);
                    return clientRepo.findById(userId);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid token format: {}", token);
                    return Optional.empty();
                }
            }
        } catch (Exception e) {
            log.error("Error finding client by token: {}", token, e);
            return Optional.empty();
        }
    }
    
    /**
     * Временное решение для поиска клиента по timestamp токену
     * В реальном приложении здесь должна быть сессия или кэш
     */
    private Optional<ClientEntity> findClientByTimestampToken(String timestamp) {
        try {
            // Для демонстрации возвращаем первого пользователя с Google ID
            // В реальном приложении здесь должна быть логика сессий
            return clientRepo.findAll().stream()
                    .filter(client -> client.getGoogleId() != null && !client.getGoogleId().isEmpty())
                    .findFirst();
        } catch (Exception e) {
            log.error("Error finding client by timestamp token: {}", timestamp, e);
            return Optional.empty();
        }
    }
    
    /**
     * Проверяет, является ли токен Google JWT токеном
     */
    private boolean isGoogleJwtToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            
            // Декодируем header
            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            JsonObject header = gson.fromJson(headerJson, JsonObject.class);
            
            // Проверяем, что это Google JWT
            String issuer = header.has("iss") ? header.get("iss").getAsString() : "";
            return issuer.equals("https://accounts.google.com");
        } catch (Exception e) {
            log.debug("Token is not a Google JWT: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Извлекает Google ID из JWT токена
     */
    private String extractGoogleIdFromJwt(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            // Декодируем payload
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonObject payload = gson.fromJson(payloadJson, JsonObject.class);
            
            // Извлекаем sub (Google ID)
            if (payload.has("sub")) {
                return payload.get("sub").getAsString();
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error extracting Google ID from JWT: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Проверяет, активен ли клиент
     */
    public boolean isClientActive(ClientEntity client) {
        return client != null && !client.getBlocked();
    }
    
    /**
     * Обновляет время последней активности клиента
     */
    public void updateLastActivity(ClientEntity client) {
        if (client != null) {
            client.setLastActivity(java.time.LocalDateTime.now());
            clientRepo.save(client);
        }
    }
    
    /**
     * Создает бессрочный токен для клиента
     */
    public String createClientToken(ClientEntity client) {
        return clientTokenService.createClientToken(client);
    }
} 