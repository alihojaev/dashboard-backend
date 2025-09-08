package com.parser.core.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientAuthResponseDto {
    private ClientDto client;
    private String token;
    private String tokenType = "Bearer";
} 