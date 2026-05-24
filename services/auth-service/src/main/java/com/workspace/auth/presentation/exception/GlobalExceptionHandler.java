package com.workspace.auth.presentation.exception;

import com.workspace.auth.domain.exception.EmailAlreadyExistsException;
import com.workspace.auth.domain.exception.InvalidEmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Bắt tất cả exception, trả về response chuẩn
// Không để Spring tự trả về error mặc định — xấu và lộ thông tin
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bắt lỗi @Valid — request không hợp lệ
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field   = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "Dữ liệu không hợp lệ", errors));
    }

    // Bắt lỗi business từ domain
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            EmailAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(409, ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmail(
            InvalidEmailException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, ex.getMessage(), null));
    }

    // Bắt tất cả lỗi còn lại — không lộ chi tiết ra ngoài
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
        log.error("Unhandled exception: ", ex); // Log đầy đủ ở server
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "Lỗi hệ thống, vui lòng thử lại sau", null));
    }

    // Response format chuẩn cho tất cả lỗi
    public record ErrorResponse(
            int status,
            String message,
            Map<String, String> errors,
            LocalDateTime timestamp
    ) {
        public static ErrorResponse of(int status, String message, Map<String, String> errors) {
            return new ErrorResponse(status, message, errors, LocalDateTime.now());
        }
    }
}