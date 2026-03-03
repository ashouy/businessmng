package com.businessmng.businessmng.auth.auth.dto;

public record AuthResponse(
    String token,
    AuthUserDto user
) {}

