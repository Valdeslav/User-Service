package com.valdeslav.user.service;

import com.valdeslav.user.dto.AuthRequest;
import com.valdeslav.user.dto.JwtResponse;
import com.valdeslav.user.exception.AuthException;
import com.valdeslav.user.exception.NotFoundException;
import com.valdeslav.user.model.RefreshToken;
import com.valdeslav.user.model.User;
import com.valdeslav.user.repository.UserRepository;
import com.valdeslav.user.security.jwt.JwtTokenService;
import com.valdeslav.user.security.jwt.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenService jwtTokenService;

    public JwtResponse login(AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new NotFoundException(String.format("User %s is not found", authRequest.getUsername())));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            log.info("User authenticated successfully: " + user.getUsername());
            RefreshToken refreshToken = refreshTokenService.create(user);

            return new JwtResponse(jwtTokenService.generateAccessToken(user), refreshToken.getValue());
        } else {
            throw new AuthException("Authorization failed. Please check your username and password");
        }
    }
}
