package com.valdeslav.user.configuration;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
@Getter
public class JwtKeysConfig {

    private final Long jwtTokenLifetime;
    private final Long refreshTokenLifetime;
    private final SecretKey jwtTokenSecretKey;
    private final SecretKey refreshTokenSecretKey;

    public JwtKeysConfig(
            @Value("${token.jwt.lifetime}") Long jwtTokenLifetime,
            @Value("${token.refresh.lifetime}") Long refreshTokenLifetime,
            @Value("${token.jwt.key}") String jwtTokenSecret,
            @Value("${token.refresh.key}") String refreshTokenSecret
    ) {
        this.jwtTokenLifetime = jwtTokenLifetime;
        this.refreshTokenLifetime = refreshTokenLifetime;
        this.jwtTokenSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtTokenSecret));
        this.refreshTokenSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshTokenSecret));
    }
}
