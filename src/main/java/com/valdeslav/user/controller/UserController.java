package com.valdeslav.user.controller;

import com.valdeslav.user.dto.enums.ResponseCode;
import com.valdeslav.user.dto.request.UserCreateDto;
import com.valdeslav.user.dto.response.SimpleResponse;
import com.valdeslav.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "User controller")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @Operation(summary = "Add a new user to the system")
    public ResponseEntity<SimpleResponse> createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        userService.create(userCreateDto);

        return new ResponseEntity<>(new SimpleResponse(ResponseCode.OK, null), HttpStatus.CREATED);
    }

    @GetMapping("/test-access")
    @Operation(summary = "Check user authorization")
    public String testAccess() {
        return "You have access!";
    }
}
