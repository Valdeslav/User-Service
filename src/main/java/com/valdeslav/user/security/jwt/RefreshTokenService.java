package com.valdeslav.user.security.jwt;

import com.valdeslav.user.model.RefreshToken;
import com.valdeslav.user.model.User;
import com.valdeslav.user.repository.RefreshTokenRepository;
import com.valdeslav.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;


@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService extends BaseTokenService {

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;

    public RefreshToken create(User user) {
        Date expiration = Date.from(
                Instant.now().plusMillis(jwtKeysConfig.getRefreshTokenLifetime()));
        String tokenValue = Jwts.builder()
                .subject(user.getUsername())
                .expiration(expiration)
                .signWith(jwtKeysConfig.getRefreshTokenSecretKey())
                .compact();

        return refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .value(tokenValue)
                        .expiryDate(expiration)
                        .build()
        );
    }

    public Optional<RefreshToken> findByValue(String value) {
        return refreshTokenRepository.findByValue(value);
    }

    public boolean validateToken(String token) {
        return validateToken(token, jwtKeysConfig.getRefreshTokenSecretKey());
    }

}
