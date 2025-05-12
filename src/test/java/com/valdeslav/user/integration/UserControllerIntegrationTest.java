package com.valdeslav.user.integration;

import com.valdeslav.user.dto.enums.ResponseCode;
import com.valdeslav.user.dto.request.AuthRequest;
import com.valdeslav.user.dto.request.UserCreateDto;
import com.valdeslav.user.dto.response.JwtResponse;
import com.valdeslav.user.dto.response.SimpleResponse;
import com.valdeslav.user.dto.response.ValidationErrorResponse;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpassword";
    private static final String EMAIL = "test@example.com";
    private static final String USER_ROLE_NAME = "USER";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser_Success() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(USERNAME);
        userCreateDto.setPassword(PASSWORD);
        userCreateDto.setEmail(EMAIL);

        ResponseEntity<SimpleResponse> response = restTemplate.postForEntity(
                "/api/v1/users/create",
                userCreateDto,
                SimpleResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ResponseCode.OK, response.getBody().getStatus());
        assertTrue(userRepository.findByUsername(USERNAME).isPresent());
    }

    @Test
    void testCreateUser_InvalidData() {
        UserCreateDto userCreateDto = new UserCreateDto();
        // Не заполняем обязательные поля

        ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity(
                "/api/v1/users/create",
                userCreateDto,
                ValidationErrorResponse.class
        );

        // Ожидаем ошибку валидации
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testTestAccess() {
        Role userRole = roleRepository.findFirstByName(USER_ROLE_NAME)
                .orElseThrow(() -> new IllegalStateException("Role " + USER_ROLE_NAME + "doesn't exist"));

        // Создаем тестового пользователя
        User user = new User(USERNAME, passwordEncoder.encode(PASSWORD), EMAIL, Set.of(userRole));
        userRepository.save(user);
        // выполняем вход
        AuthRequest authRequest = new AuthRequest(USERNAME, PASSWORD);
        JwtResponse loginResponse = restTemplate.postForObject("/api/v1/login", authRequest, JwtResponse.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResponse.getAccessToken());

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/users/test-access",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("You have access!", response.getBody());
        refreshTokenRepository.deleteAll();
    }
} 