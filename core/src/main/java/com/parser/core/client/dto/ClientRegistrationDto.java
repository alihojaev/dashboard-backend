package com.parser.core.client.dto;

import com.parser.core.client.entity.ClientEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegistrationDto {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
    
    // Для email авторизации
    private String password;
    
    // Для Google OAuth
    private String googleId;
    private String googleEmail;
    

    
    @NotNull(message = "Auth type is required")
    private ClientEntity.AuthType authType;
    
    // Валидация в зависимости от типа авторизации
    public boolean isValidForAuthType() {
        switch (authType) {
            case EMAIL:
                return password != null && !password.trim().isEmpty();
            case GOOGLE:
                return googleId != null && !googleId.trim().isEmpty();
            default:
                return false;
        }
    }
} 