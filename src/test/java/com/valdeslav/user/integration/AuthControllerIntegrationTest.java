package com.valdeslav.user.integration;

import com.valdeslav.user.dto.enums.ResponseCode;
import com.valdeslav.user.dto.request.AuthRequest;
import com.valdeslav.user.dto.request.RefreshTokenRequest;
import com.valdeslav.user.dto.response.JwtResponse;
import com.valdeslav.user.dto.response.SimpleResponse;
import com.valdeslav.user.model.Role;
import com.valdeslav.user.model.User;
import com.valdeslav.user.repository.RefreshTokenRepository;
import com.valdeslav.user.repository.RoleRepository;
import com.valdeslav.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpassword";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String USER_ROLE_NAME = "USER";
    private static Role userRole;
    private static User user;

    @BeforeEach
    void setUp() {
        userRole = roleRepository.findFirstByName(USER_ROLE_NAME)
                .orElseThrow(() -> new IllegalStateException("Role " + USER_ROLE_NAME + "doesn't exist"));

        // Создаем тестового пользователя
        user = new User(TEST_USERNAME, passwordEncoder.encode(TEST_PASSWORD), TEST_EMAIL, Set.of(userRole));
        userRepository.save(user);
    }

    @AfterEach
    void clear() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testLogin_Success() {
        AuthRequest authRequest = new AuthRequest(TEST_USERNAME, TEST_PASSWORD);

        ResponseEntity<JwtResponse> response = restTemplate.postForEntity("/api/v1/login", authRequest, JwtResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getAccessToken());
        assertNotNull(response.getBody().getRefreshToken());
    }

    @Test
    void testLogin_WrongPassword() {
        AuthRequest authRequest = new AuthRequest(TEST_USERNAME, "wrongpassword");

        ResponseEntity<SimpleResponse> response = restTemplate.postForEntity("/api/v1/login", authRequest, SimpleResponse.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testRefreshToken_Success() {
        AuthRequest authRequest = new AuthRequest(TEST_USERNAME, TEST_PASSWORD);
        JwtResponse loginResponse = restTemplate.postForObject("/api/v1/login", authRequest, JwtResponse.class);

        // Подготавливаем запрос на обновление токена
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(loginResponse.getRefreshToken());
        ResponseEntity<JwtResponse> response = restTemplate.postForEntity("/api/v1/refresh-token", refreshRequest, JwtResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getAccessToken());
        assertNotNull(response.getBody().getRefreshToken());
    }

    @Test
    void testLogout_Success() {
        // Сначала получаем токены через логин
        AuthRequest authRequest = new AuthRequest(TEST_USERNAME, TEST_PASSWORD);
        JwtResponse loginResponse = restTemplate.postForObject("/api/v1/login", authRequest, JwtResponse.class);

        // Подготавливаем запрос на выход
        RefreshTokenRequest logoutRequest = new RefreshTokenRequest(loginResponse.getRefreshToken());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResponse.getAccessToken());
        HttpEntity<RefreshTokenRequest> requestEntity = new HttpEntity<>(logoutRequest, headers);
        ResponseEntity<SimpleResponse> response = restTemplate.postForEntity("/api/v1/logout", requestEntity, SimpleResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ResponseCode.OK, response.getBody().getStatus());

        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(loginResponse.getRefreshToken());
        ResponseEntity<SimpleResponse> refreshResponse = restTemplate.postForEntity(
                "/api/v1/refresh-token",
                refreshRequest,
                SimpleResponse.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, refreshResponse.getStatusCode());
    }
}