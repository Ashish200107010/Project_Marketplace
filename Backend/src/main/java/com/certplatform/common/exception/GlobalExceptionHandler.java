package com.certplatform.common.exception;

import com.certplatform.common.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.JwtException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ✅ Validation errors (bad input)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult().getFieldErrors()
                       .stream()
                       .map(err -> err.getField() + ": " + err.getDefaultMessage())
                       .collect(Collectors.joining(", "));

        log.warn("[Validation] Failed at {}: {}", request.getRequestURI(), msg, ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                msg,
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }

    // ✅ Business logic errors (explicit ResponseStatusException thrown in services)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatusCode status = ex.getStatusCode();
        log.warn("[Business] Error at {}: {}", request.getRequestURI(), ex.getReason(), ex);

        ErrorResponse error = new ErrorResponse(
                status.value(),
                ((HttpStatus) status).getReasonPhrase(),
                ex.getReason(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    // ✅ Entity not found (JPA)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        log.warn("[EntityNotFound] At {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ✅ JWT / Security errors
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwt(JwtException ex, HttpServletRequest request) {
        log.error("[JWT] Invalid or expired token at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Invalid or expired authentication token",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ✅ Generic fallback (unexpected errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("[Generic] Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Something went wrong. Please try again later.",
                request.getRequestURI()
        );
        return ResponseEntity.internalServerError().body(error);
    }
}
