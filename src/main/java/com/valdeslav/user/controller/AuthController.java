package com.valdeslav.user.controller;

import com.valdeslav.user.dto.enums.ResponseCode;
import com.valdeslav.user.dto.request.AuthRequest;
import com.valdeslav.user.dto.request.RefreshTokenRequest;
import com.valdeslav.user.dto.response.JwtResponse;
import com.valdeslav.user.dto.response.SimpleResponse;
import com.valdeslav.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Tag(name = "Аутентификация пользователя")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User authentication", description = "Returns access and refresh tokens")
    public JwtResponse authenticate(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out", description = "After this operation access and refresh tokens are invalidated")
    public SimpleResponse logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        authService.logout(refreshTokenRequest);

        return new SimpleResponse(ResponseCode.OK, null);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }
}
