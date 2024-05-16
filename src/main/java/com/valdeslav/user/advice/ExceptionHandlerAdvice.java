package com.valdeslav.user.advice;

import com.valdeslav.user.dto.enums.ResponseStatus;
import com.valdeslav.user.dto.response.SimpleResponse;
import com.valdeslav.user.exception.AuthException;
import com.valdeslav.user.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(value = { NotFoundException.class })
    public ResponseEntity<SimpleResponse> handleNotFound(NotFoundException exception) {
        return new ResponseEntity<>(new SimpleResponse(ResponseStatus.ERROR, exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { AuthException.class })
    public ResponseEntity<SimpleResponse> handleAuthException(AuthException exception) {
        return new ResponseEntity<>(new SimpleResponse(ResponseStatus.UNAUTHORIZED, exception.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}