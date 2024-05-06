package com.valdeslav.user.security.jwt;

import com.valdeslav.user.model.User;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

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
                .claim("roles", user.getRoles())
                .compact();
    }
}
