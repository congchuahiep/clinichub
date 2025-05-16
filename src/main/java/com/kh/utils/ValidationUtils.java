package com.kh.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ValidationUtils {
    @Autowired
    private Validator validator;

    /**
     * Validate dữ liệu của một DTO một cách tự động
     * 
     * @param <T> Loại của DTO cần validate
     * @param dto - DTO cần validate
     * @return Một {@code ResponseEntity}, có thể sử dụng để trả về response thông
     *         báo lỗi cho người dùng
     */
    public <T> ResponseEntity<?> getValidationErrorResponse(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (violations.isEmpty())
            return null;
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<T> violation : violations) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}