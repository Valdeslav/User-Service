package com.valdeslav.user.advice;

import com.valdeslav.user.dto.enums.ResponseCode;
import com.valdeslav.user.dto.response.SimpleResponse;
import com.valdeslav.user.dto.response.ValidationErrorResponse;
import com.valdeslav.user.exception.AuthException;
import com.valdeslav.user.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.View;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = { NotFoundException.class })
    public ResponseEntity<SimpleResponse> handleNotFound(NotFoundException exception) {
        return new ResponseEntity<>(new SimpleResponse(ResponseCode.ERROR, exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = { AuthException.class, AuthenticationException.class })
    public ResponseEntity<SimpleResponse> handleAuthException(Exception exception) {
        return new ResponseEntity<>(new SimpleResponse(
                ResponseCode.UNAUTHORIZED,
                "Authentication error: " + exception.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "invalid"));
        return new ResponseEntity<>(
                new ValidationErrorResponse(ResponseCode.VALIDATION_ERROR, errors),
                HttpStatus.BAD_REQUEST
        );
    }
}