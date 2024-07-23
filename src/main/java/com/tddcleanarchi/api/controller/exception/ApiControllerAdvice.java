package com.tddcleanarchi.api.controller.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler({MissingPathVariableException.class, NoResourceFoundException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleMissingPathVariableException(Exception ex, WebRequest request) {
        return createErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({EntityNotFoundException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception ex, WebRequest request) {
        HttpStatus status = ex instanceof EntityNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return createErrorResponse(ex, request, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, WebRequest request) {
        return createErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(Exception ex, WebRequest request, HttpStatus status) {
        log.error("Exception occurred: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path
    ) {}
}

