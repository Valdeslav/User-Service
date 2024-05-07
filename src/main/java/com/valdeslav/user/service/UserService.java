package com.valdeslav.user.service;

import com.valdeslav.user.dto.UserCreateDto;
import com.valdeslav.user.model.User;
import com.valdeslav.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(UserCreateDto userCreateDto) {
        return new User();
    }
}
