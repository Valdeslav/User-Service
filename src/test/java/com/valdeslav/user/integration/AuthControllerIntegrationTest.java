package com.valdeslav.user.integration;

import com.valdeslav.user.dto.enums.ResponseCode;
import com.valdeslav.user.dto.request.AuthRequest;
import com.valdeslav.user.dto.request.RefreshTokenRequest;
import com.valdeslav.user.dto.response.JwtResponse;
import com.valdeslav.user.dto.response.SimpleResponse;
import com.valdeslav.user.model.Role;
import com.valdeslav.user.model.User;
import com.valdeslav.user.repository.RoleRepository;
import com.valdeslav.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
    private PasswordEncoder passwordEncoder;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpassword";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String USER_ROLE_NAME = "USER";
    private static Role userRole;
    private static User user;

    @BeforeEach
    void setUp() {
        // Создаем роль пользователя
        userRole = roleRepository.findFirstByName(USER_ROLE_NAME)
                .orElseThrow(() -> new IllegalStateException("Role " + USER_ROLE_NAME + "doesn't exist"));

        // Создаем тестового пользователя
        user = new User(TEST_USERNAME, passwordEncoder.encode(TEST_PASSWORD), TEST_EMAIL, Set.of(userRole));
        userRepository.save(user);
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testLogin_Success() {
        // Подготовка
        AuthRequest authRequest = new AuthRequest(TEST_USERNAME, TEST_PASSWORD);

        // Выполнение
        ResponseEntity<JwtResponse> response = restTemplate.postForEntity("/api/v1/login", authRequest, JwtResponse.class);

        // Проверка
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getAccessToken());
        assertNotNull(response.getBody().getRefreshToken());
    }

    @Test
    void testLogin_WrongPassword() {
        // Подготовка
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(TEST_USERNAME);
        authRequest.setPassword("wrongpassword");

        // Выполнение
        ResponseEntity<JwtResponse> response = restTemplate.postForEntity(
                "/api/v1/login",
                authRequest,
                JwtResponse.class
        );

        // Проверка
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testRefreshToken_Success() {
        // Подготовка
        // Сначала получаем токены через логин
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(TEST_USERNAME);
        authRequest.setPassword(TEST_PASSWORD);
        JwtResponse loginResponse = restTemplate.postForObject(
                "/api/v1/login",
                authRequest,
                JwtResponse.class
        );

        // Подготавливаем запрос на обновление токена
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setValue(loginResponse.getRefreshToken());

        // Выполнение
        ResponseEntity<JwtResponse> response = restTemplate.postForEntity(
                "/api/v1/refresh-token",
                refreshRequest,
                JwtResponse.class
        );

        // Проверка
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getAccessToken());
        assertNotNull(response.getBody().getRefreshToken());
        assertNotEquals(loginResponse.getAccessToken(), response.getBody().getAccessToken());
    }

    @Test
    void testLogout_Success() {
        // Подготовка
        // Сначала получаем токены через логин
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(TEST_USERNAME);
        authRequest.setPassword(TEST_PASSWORD);
        JwtResponse loginResponse = restTemplate.postForObject(
                "/api/v1/login",
                authRequest,
                JwtResponse.class
        );

        // Подготавливаем запрос на выход
        RefreshTokenRequest logoutRequest = new RefreshTokenRequest();
        logoutRequest.setValue(loginResponse.getRefreshToken());

        // Выполнение
        ResponseEntity<SimpleResponse> response = restTemplate.postForEntity(
                "/api/v1/logout",
                logoutRequest,
                SimpleResponse.class
        );

        // Проверка
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ResponseCode.OK, response.getBody().getStatus());

        // Проверяем, что токен больше не работает
        ResponseEntity<JwtResponse> refreshResponse = restTemplate.postForEntity(
                "/api/v1/refresh-token",
                logoutRequest,
                JwtResponse.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, refreshResponse.getStatusCode());
    }
}