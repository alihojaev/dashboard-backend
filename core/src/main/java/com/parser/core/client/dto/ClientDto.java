package com.parser.core.client.dto;

import com.parser.core.client.entity.ClientEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    
    private UUID id;
    private String email;
    private String username;
    private String phone;
    private String googleId;
    private String googleEmail;
    private Boolean blocked;
    private ClientEntity.AuthType authType;
    private LocalDateTime lastActivity;
    private LocalDateTime cdt;
    private LocalDateTime mdt;
    
    public static ClientDto fromEntity(ClientEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new ClientDto(
                entity.getId(),
                entity.getEmail(),
                entity.getUsername(),
                entity.getPhone(),
                entity.getGoogleId(),
                entity.getGoogleEmail(),
                entity.getBlocked(),
                entity.getAuthType(),
                entity.getLastActivity(),
                entity.getCdt(),
                entity.getMdt()
        );
    }
} 