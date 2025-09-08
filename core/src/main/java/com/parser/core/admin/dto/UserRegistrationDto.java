package com.parser.core.admin.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String firstName;
    private String lastName;
    private String phone;
    private String username;
    private String password;
    private String email;
} 