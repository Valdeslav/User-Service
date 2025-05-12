package com.valdeslav.user.dto.response;

import com.valdeslav.user.dto.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ValidationErrorResponse {
    private ResponseCode status;
    private Map<String, String> errors;
}
