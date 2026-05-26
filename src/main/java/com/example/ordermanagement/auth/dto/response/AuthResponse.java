package com.example.ordermanagement.auth.dto;

import com.example.ordermanagement.auth.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {

    private String accessToken;

    private String tokenType;

    private Long userId;

    private String fullName;

    private String email;

    private Role role;
}