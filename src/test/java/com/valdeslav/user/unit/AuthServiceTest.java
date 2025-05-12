package com.valdeslav.user.unit;

import com.valdeslav.user.dto.request.AuthRequest;
import com.valdeslav.user.dto.request.RefreshTokenRequest;
import com.valdeslav.user.dto.response.JwtResponse;
import com.valdeslav.user.exception.AuthException;
import com.valdeslav.user.model.RefreshToken;
import com.valdeslav.user.model.User;
import com.valdeslav.user.repository.UserRepository;
import com.valdeslav.user.security.JwtUserDetails;
import com.valdeslav.user.security.jwt.JwtTokenService;
import com.valdeslav.user.security.jwt.RefreshTokenService;
import com.valdeslav.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password";
    private static final String REFRESH_TOKEN_VALUE = "refreshTokenValue";
    private static final String ACCESS_TOKEN = "accessToken";

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private JwtTokenService jwtTokenService;
    @InjectMocks
    private AuthService authService;

    private User user;
    private AuthRequest authRequest;
    private RefreshToken refreshToken;
    private JwtResponse jwtResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword("encodedPassword");

        authRequest = new AuthRequest();
        authRequest.setUsername(TEST_USERNAME);
        authRequest.setPassword(TEST_PASSWORD);

        refreshToken = new RefreshToken();
        refreshToken.setValue(REFRESH_TOKEN_VALUE);
        refreshToken.setUser(user);

        jwtResponse = new JwtResponse(ACCESS_TOKEN, REFRESH_TOKEN_VALUE);
    }

    @Test
    void login_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(refreshTokenService.create(user)).thenReturn(refreshToken);
        when(jwtTokenService.generateAccessToken(user)).thenReturn(ACCESS_TOKEN);

        JwtResponse response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals(ACCESS_TOKEN, response.getAccessToken());
        assertEquals(REFRESH_TOKEN_VALUE, response.getRefreshToken());
        verify(refreshTokenService).create(user);
    }

    @Test
    void login_Failure() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

        assertThrows(AuthException.class, () -> authService.login(authRequest));
    }

    @Test
    void refreshToken_Success() {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setValue(REFRESH_TOKEN_VALUE);

        when(refreshTokenService.validateToken(REFRESH_TOKEN_VALUE)).thenReturn(true);
        when(refreshTokenService.findByValue(REFRESH_TOKEN_VALUE)).thenReturn(Optional.of(refreshToken));
        when(jwtTokenService.generateAccessToken(user)).thenReturn(ACCESS_TOKEN);

        JwtResponse response = authService.refreshToken(refreshTokenRequest);

        assertNotNull(response);
        assertEquals(ACCESS_TOKEN, response.getAccessToken());
        assertEquals(REFRESH_TOKEN_VALUE, response.getRefreshToken());
    }

    @Test
    void refreshToken_InvalidToken() {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setValue("invalidToken");

        when(refreshTokenService.validateToken("invalidToken")).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.refreshToken(refreshTokenRequest));
    }

    @Test
    void logout_Success() {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setValue(REFRESH_TOKEN_VALUE);

        JwtUserDetails jwtUserDetails = new JwtUserDetails(user);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetails);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

        authService.logout(refreshTokenRequest);

        verify(refreshTokenService).deleteByUserAndValue(user, REFRESH_TOKEN_VALUE);
    }
}