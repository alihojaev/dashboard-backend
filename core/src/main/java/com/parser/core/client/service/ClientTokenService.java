package com.parser.core.client.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.parser.core.client.entity.ClientEntity;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class ClientTokenService {

    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_EMAIL = "client_email";
    private static final String CLIENT_TYPE = "client_type";
    private static final String TOKEN_TYPE = "client";

    String secret;

    ClientTokenService(@Value("${jwt.secret:default-secret-key-for-clients}") final String secret) {
        this.secret = secret;
    }

    /**
     * Создает бессрочный токен для клиента
     */
    public String createClientToken(ClientEntity client) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim(CLIENT_ID, client.getId().toString())
                .withClaim(CLIENT_EMAIL, client.getEmail())
                .withClaim(CLIENT_TYPE, client.getAuthType().toString())
                .withIssuedAt(new Date())
                .withIssuer("client-api")
                .withSubject(client.getId().toString())
                .sign(algorithm);
    }

    /**
     * Проверяет токен клиента
     */
    public boolean verifyClientToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("client-api")
                    .build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.error("verifyClientToken(): exc: {}, msg: {}", e.getClass().getCanonicalName(), e.getMessage());
            return false;
        }
    }

    /**
     * Извлекает ID клиента из токена
     */
    public UUID getClientId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            String clientIdStr = jwt.getClaim(CLIENT_ID).asString();
            return clientIdStr != null ? UUID.fromString(clientIdStr) : null;
        } catch (JWTDecodeException | IllegalArgumentException e) {
            log.error("getClientId(): exc: {}, msg: {}", e.getClass().getCanonicalName(), e.getMessage());
            return null;
        }
    }

    /**
     * Извлекает email клиента из токена
     */
    public String getClientEmail(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(CLIENT_EMAIL).asString();
        } catch (JWTDecodeException e) {
            log.error("getClientEmail(): exc: {}, msg: {}", e.getClass().getCanonicalName(), e.getMessage());
            return null;
        }
    }

    /**
     * Проверяет, является ли токен клиентским токеном
     */
    public boolean isClientToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            String issuer = jwt.getIssuer();
            return "client-api".equals(issuer);
        } catch (JWTDecodeException e) {
            return false;
        }
    }
} 