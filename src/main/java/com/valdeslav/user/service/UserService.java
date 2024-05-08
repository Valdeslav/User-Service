package com.valdeslav.user.service;

import com.valdeslav.user.dto.request.UserCreateDto;
import com.valdeslav.user.model.Role;
import com.valdeslav.user.model.User;
import com.valdeslav.user.model.enums.Roles;
import com.valdeslav.user.repository.RoleRepository;
import com.valdeslav.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User create(UserCreateDto userCreateDto) {
        Role role = roleRepository.findFirstByName(Roles.USER.name()).orElse(null);
        if (role == null) {
            log.warn("Cannot find role 'USER'. User role will be empty!");
        }

        User user = User.builder()
                .username(userCreateDto.getUsername())
                .password(new BCryptPasswordEncoder().encode(userCreateDto.getPassword()))
                .email(userCreateDto.getEmail())
                .roles(role != null ? Collections.singleton(role) : Collections.emptySet())
                .build();

        return userRepository.save(user);
    }
}
