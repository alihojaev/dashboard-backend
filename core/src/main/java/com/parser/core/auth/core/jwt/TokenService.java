package com.parser.core.auth.core.jwt;

public interface TokenService {

    boolean verify(String token, String username, String secret);

    String sign(String username, String secret);

    String getUsername(String token);
}
