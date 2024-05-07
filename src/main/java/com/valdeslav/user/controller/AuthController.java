package com.valdeslav.user.controller;

import com.valdeslav.user.dto.AuthRequest;
import com.valdeslav.user.dto.JwtResponse;
import com.valdeslav.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponse authenticate(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }
}
