package com.valdeslav.user.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Class<?> entity, Long id) {
        super(String.format("%s with id %d is not found", entity.getName(), id));
    }

    public NotFoundException(String message) {
        super(message);
    }
}
