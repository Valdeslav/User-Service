package com.valdeslav.user.service;

import com.valdeslav.user.exception.NotFoundException;
import com.valdeslav.user.model.RefreshToken;
import com.valdeslav.user.repository.RefreshTokenRepository;
import com.valdeslav.user.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
@Getter
@Setter
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    @Value("${token.refresh.lifetime}")
    private Long refreshTokenLifetime;

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;

    public RefreshToken create(String username) {
        return RefreshToken.builder()
                .user(userRepository.findByUsername(username)
                        .orElseThrow(() -> new NotFoundException(String.format("User %s is not found", username))))
                .value(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenLifetime))
                .build();
    }

    public Optional<RefreshToken> findByValue(String value) {
        return refreshTokenRepository.findByValue(value);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getValue() + " Refresh token is expired. Please make a new login..!");
        }

        return token;
    }
}
