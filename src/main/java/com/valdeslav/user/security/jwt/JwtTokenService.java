package com.valdeslav.user.security.jwt;

import com.valdeslav.user.model.Role;
import com.valdeslav.user.model.User;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JwtTokenService extends BaseTokenService {

    public String generateAccessToken(User user) {
        Date expiration = Date.from(
                Instant.now().plusMillis(jwtKeysConfig.getJwtTokenLifetime()));

        return Jwts.builder()
                .subject(user.getUsername())
                .expiration(expiration)
                .signWith(jwtKeysConfig.getJwtTokenSecretKey())
                .claim("roles", user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .compact();
    }

    public boolean validateToken(String token) {
        return validateToken(token, jwtKeysConfig.getJwtTokenSecretKey());
    }

    public String extractUsername(String token) {
        return extractUsername(token, jwtKeysConfig.getJwtTokenSecretKey());
    }
}
