package com.parser.core.auth.core.dto;

import lombok.Value;

@Value
public class TokenDto {
    String token;
    String refreshToken;
}
