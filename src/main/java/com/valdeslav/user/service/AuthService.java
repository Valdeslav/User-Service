package com.valdeslav.user.service;

import com.valdeslav.user.dto.request.AuthRequest;
import com.valdeslav.user.dto.request.RefreshTokenRequest;
import com.valdeslav.user.dto.response.JwtResponse;
import com.valdeslav.user.exception.AuthException;
import com.valdeslav.user.exception.NotFoundException;
import com.valdeslav.user.model.RefreshToken;
import com.valdeslav.user.model.User;
import com.valdeslav.user.repository.UserRepository;
import com.valdeslav.user.security.JwtUserDetails;
import com.valdeslav.user.security.jwt.JwtTokenService;
import com.valdeslav.user.security.jwt.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenService jwtTokenService;

    public JwtResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        Optional<User> userOptional = userRepository.findByUsername(authRequest.getUsername());

        if (userOptional.isPresent() && authentication.isAuthenticated()) {
            User user = userOptional.get();
            log.info("User authenticated successfully: " + user.getUsername());
            RefreshToken refreshToken = refreshTokenService.create(user);

            return new JwtResponse(jwtTokenService.generateAccessToken(user), refreshToken.getValue());
        } else {
            throw new AuthException("Authentication error: Bad credentials");
        }
    }

    public JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        if (!refreshTokenService.validateToken(refreshTokenRequest.getValue())) {
            throw new AuthException("Invalid refresh token. Please authorize again.");
        }

        RefreshToken refreshToken = refreshTokenService.findByValue(refreshTokenRequest.getValue())
                .orElseThrow(() -> new AuthException("Cannot refresh token."));

        return new JwtResponse(jwtTokenService.generateAccessToken(refreshToken.getUser()), refreshToken.getValue());
    }

    public void logout(RefreshTokenRequest refreshTokenRequest) {
        User user = userRepository.findByUsername(getCurrentUserName()).orElse(null);

        refreshTokenService.deleteByUserAndValue(user, refreshTokenRequest.getValue());

    }

    public String getCurrentUserName() {
        return ((JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }
}
