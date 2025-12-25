package com.karan.craftingtable.advices;

import com.karan.craftingtable.enums.ErrorCodeEnum;
import com.karan.craftingtable.exceptions.BadRequestException;
import com.karan.craftingtable.exceptions.ResourceNotFoundException;
import com.karan.craftingtable.exceptions.UnauthorizedException;
import com.karan.craftingtable.models.wrappers.APIError;
import com.karan.craftingtable.models.wrappers.APIResponse;
import com.karan.craftingtable.models.wrappers.FieldValidationError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldValidationError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldValidationError(
                        err.getField(),
                        err.getRejectedValue(),
                        err.getDefaultMessage()
                ))
                .toList();
        APIError apiError = new APIError(
                ErrorCodeEnum.VALIDATION_ERROR.name(),
                "Validation failed",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                fieldErrors
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.failure(apiError));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        List<FieldValidationError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(cv -> new FieldValidationError(
                        cv.getPropertyPath().toString(),
                        cv.getInvalidValue(),
                        cv.getMessage()
                ))
                .toList();
        APIError apiError = new APIError(
                ErrorCodeEnum.CONSTRAINT_VIOLATION.name(),
                "Validation failed",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                fieldErrors
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.failure(apiError));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<APIResponse<Object>> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        APIError apiError = new APIError(
                ErrorCodeEnum.BAD_REQUEST.name(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                null
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.failure(apiError));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        APIError apiError = new APIError(
                ErrorCodeEnum.NOT_FOUND.name(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.NOT_FOUND.value(),
                null
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(APIResponse.failure(apiError));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<APIResponse<Object>> handleUnauthorizedException(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        APIError apiError = new APIError(
                ErrorCodeEnum.UNAUTHORIZED.name(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.FORBIDDEN.value(),
                null
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(APIResponse.failure(apiError));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Object>> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        APIError apiError = new APIError(
                ErrorCodeEnum.INTERNAL_SERVER_ERROR.name(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.failure(apiError));
    }

}
