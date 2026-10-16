package com.vaultiq.sentinel.exception;

import com.vaultiq.sentinel.dto.ErrorResponse;
import com.vaultiq.sentinel.dto.InjectionBlockedException;
import com.vaultiq.sentinel.dto.UpstreamServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InjectionBlockedException.class)
    public ResponseEntity<ErrorResponse> injectionBlocked(InjectionBlockedException exception) {
        return response(HttpStatus.FORBIDDEN, "injection-attempt", exception.getMessage());
    }

    @ExceptionHandler(UpstreamServiceException.class)
    public ResponseEntity<ErrorResponse> upstreamFailure(UpstreamServiceException exception) {
        return response(HttpStatus.BAD_GATEWAY, "upstream-failure", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidRequest(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst().map(error -> error.getDefaultMessage()).orElse("Invalid request");
        return response(HttpStatus.BAD_REQUEST, "invalid-request", message);
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(Instant.now(), status.value(), error, message));
    }
}
