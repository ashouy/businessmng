package com.businessmng.businessmng.auth.auth;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.businessmng.businessmng.auth.auth.dto.AuthUserDto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final SecretKey key;

  public JwtService(@Value("${app.jwt.secret}") String secret) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateToken(AuthUserDto user, long minutes) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(user.email())
        .claim("name", user.name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(minutes, ChronoUnit.MINUTES)))
        .signWith(key)
        .compact();
  }

  public AuthUserDto validateAndGetUser(String token) {
    var claims = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    String email = claims.getSubject();
    String name = claims.get("name", String.class);
    return new AuthUserDto(name, email);
  }
}

