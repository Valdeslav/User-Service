package com.valdeslav.user.security.jwt;

import com.valdeslav.user.configuration.JwtKeysConfig;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.SecretKey;

@Slf4j
public abstract class BaseTokenService {

    @Autowired
    protected JwtKeysConfig jwtKeysConfig;

    protected boolean validateToken(String token, SecretKey secretKey) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported jwt: " + e.getMessage());
        } catch (MalformedJwtException e) {
            log.info("Malformed jwt: " + e.getMessage());
        } catch (Exception e) {
            log.info("invalid token: " + e.getMessage());
        }

        return false;
    }

    protected String extractUsername(String token, SecretKey secretKey) {
        return getClaims(token, secretKey).getSubject();
    }

    protected Claims getClaims(String token, SecretKey secretKey) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
