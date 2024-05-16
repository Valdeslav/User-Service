package com.valdeslav.user.controller;

import com.valdeslav.user.dto.enums.ResponseStatus;
import com.valdeslav.user.dto.request.UserCreateDto;
import com.valdeslav.user.dto.response.SimpleResponse;
import com.valdeslav.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<SimpleResponse> createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        userService.create(userCreateDto);

        return new ResponseEntity<>(new SimpleResponse(ResponseStatus.OK, null), HttpStatus.CREATED);
    }

    @GetMapping("/test-access")
    public String testAccess() {
        return "You have access!";
    }
}
