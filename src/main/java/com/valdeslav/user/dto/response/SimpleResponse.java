package com.valdeslav.user.dto.response;

import com.valdeslav.user.dto.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SimpleResponse {
    private ResponseStatus status;
    private String message;
}
