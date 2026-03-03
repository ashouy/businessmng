package com.businessmng.businessmng.auth.auth;

import java.time.Duration;
import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.businessmng.businessmng.auth.auth.dto.AuthResponse;
import com.businessmng.businessmng.auth.auth.dto.AuthUserDto;
import com.businessmng.businessmng.auth.auth.dto.LoginRequest;

@Service
public class AuthService {

  public static final String JWT_COOKIE_NAME = "bm_jwt";

  private final JwtService jwtService;

  public AuthService(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  public Optional<AuthResponse> login(LoginRequest request, boolean secureCookie, String cookiePath) {
    // TODO: Replace with real user lookup and password check.
    if (!"admin".equals(request.login()) || !"admin".equals(request.senha())) {
      return Optional.empty();
    }

    AuthUserDto user = new AuthUserDto("Administrador", "admin@example.com");
    String token = jwtService.generateToken(user, 60); // 60 minutes

    return Optional.of(new AuthResponse(token, user));
  }

  public Optional<AuthResponse> refresh(String token, boolean secureCookie, String cookiePath) {
    try {
      AuthUserDto user = jwtService.validateAndGetUser(token);
      String newToken = jwtService.generateToken(user, 60);
      return Optional.of(new AuthResponse(newToken, user));
    } catch (Exception ex) {
      return Optional.empty();
    }
  }

  public ResponseCookie buildJwtCookie(String token, boolean secureCookie, String cookiePath) {
    return ResponseCookie.from(JWT_COOKIE_NAME, token)
        .httpOnly(true)
        .secure(secureCookie)
        .path(cookiePath)
        .sameSite("Strict")
        .maxAge(Duration.ofMinutes(60))
        .build();
  }

  public ResponseCookie buildClearCookie(String cookiePath, boolean secureCookie) {
    return ResponseCookie.from(JWT_COOKIE_NAME, "")
        .httpOnly(true)
        .secure(secureCookie)
        .path(cookiePath)
        .sameSite("Strict")
        .maxAge(Duration.ZERO)
        .build();
  }
}

