package com.valdeslav.user.unit;

import com.valdeslav.user.dto.request.UserCreateDto;
import com.valdeslav.user.model.Role;
import com.valdeslav.user.model.User;
import com.valdeslav.user.model.enums.Roles;
import com.valdeslav.user.repository.RoleRepository;
import com.valdeslav.user.repository.UserRepository;
import com.valdeslav.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserService userService;

    private UserCreateDto userCreateDto;
    private Role role;

    private static final String TEST_USERNAME = "newuser";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_EMAIL = "newuser@example.com";

    @BeforeEach
    void setUp() {
        userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(TEST_USERNAME);
        userCreateDto.setPassword(TEST_PASSWORD);
        userCreateDto.setEmail(TEST_EMAIL);

        role = new Role();
        role.setName(Roles.USER.name());
    }

    @Test
    void create_Success() {
        when(roleRepository.findFirstByName(Roles.USER.name())).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.create(userCreateDto);

        assertNotNull(user);
        assertEquals(TEST_USERNAME, user.getUsername());
        assertTrue(new BCryptPasswordEncoder().matches(TEST_PASSWORD, user.getPassword()));
        assertEquals(TEST_EMAIL, user.getEmail());
        assertTrue(user.getRoles().contains(role));
        verify(userRepository).save(user);
    }

    @Test
    void create_RoleNotFound() {
        when(roleRepository.findFirstByName(Roles.USER.name())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.create(userCreateDto);

        assertNotNull(user);
        assertEquals(TEST_USERNAME, user.getUsername());
        assertTrue(new BCryptPasswordEncoder().matches(TEST_PASSWORD, user.getPassword()));
        assertEquals(TEST_EMAIL, user.getEmail());
        assertTrue(user.getRoles().isEmpty());
        verify(userRepository).save(user);
    }
}