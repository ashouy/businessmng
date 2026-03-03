package com.businessmng.businessmng.auth.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.businessmng.businessmng.auth.auth.dto.AuthResponse;
import com.businessmng.businessmng.auth.auth.dto.LoginRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final boolean secureCookie;
  private final String cookiePath;

  public AuthController(
      AuthService authService,
      @Value("${app.auth.cookie.secure:true}") boolean secureCookie,
      @Value("${app.auth.cookie.path:/}") String cookiePath
  ) {
    this.authService = authService;
    this.secureCookie = secureCookie;
    this.cookiePath = cookiePath;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request, secureCookie, cookiePath)
        .map(authResponse -> {
          var cookie = authService.buildJwtCookie(authResponse.token(), secureCookie, cookiePath);
          var body = new AuthResponse(null, authResponse.user());
          return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, cookie.toString())
              .body(body);
        })
        .orElseGet(() -> ResponseEntity.status(401).build());
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
    String token = extractTokenFromCookies(request);
    if (token == null || token.isBlank()) {
      return ResponseEntity.ok(null);
    }

    return authService.refresh(token, secureCookie, cookiePath)
        .map(authResponse -> {
          var cookie = authService.buildJwtCookie(authResponse.token(), secureCookie, cookiePath);
          var body = new AuthResponse(null, authResponse.user());
          return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, cookie.toString())
              .body(body);
        })
        .orElseGet(() -> {
          var clearCookie = authService.buildClearCookie(cookiePath, secureCookie);
          return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
              .body(null);
        });
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    var clearCookie = authService.buildClearCookie(cookiePath, secureCookie);
    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
        .build();
  }

  private String extractTokenFromCookies(HttpServletRequest request) {
    if (request.getCookies() == null) return null;
    return Arrays.stream(request.getCookies())
        .filter(c -> AuthService.JWT_COOKIE_NAME.equals(c.getName()))
        .findFirst()
        .map(Cookie::getValue)
        .orElse(null);
  }
}

