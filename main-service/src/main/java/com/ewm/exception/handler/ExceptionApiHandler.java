package com.ewm.exception.handler;

import com.ewm.exception.ConfilctException;
import com.ewm.exception.NotExistsExeption;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionApiHandler {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
        UnsupportedOperationException.class, IllegalArgumentException.class, MethodArgumentTypeMismatchException.class,
    MissingServletRequestParameterException.class})
    public ApiError handleValidationExceptions(Throwable ex) {
        ApiError apiError = ApiError.builder()
            .errors(Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()))
            .message(ex.getMessage())
            .status(HttpStatus.BAD_REQUEST.toString())
            .timestamp(LocalDateTime.now().format(formatter)).build();
        apiError.log();
        return apiError;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotExistsExeption.class})
    public ApiError handleNotFoundExceptions(RuntimeException ex) {
        ApiError apiError = ApiError.builder()
            .errors(Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()))
            .message(ex.getMessage())
            .status(HttpStatus.NOT_FOUND.toString())
            .timestamp(LocalDateTime.now().format(formatter)).build();
        apiError.log();
        return apiError;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({ConfilctException.class})
    public ApiError handleConflictExceptions(RuntimeException ex) {
        ApiError apiError = ApiError.builder()
            .errors(Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()))
            .message(ex.getMessage())
            .status(HttpStatus.NOT_FOUND.toString())
            .timestamp(LocalDateTime.now().format(formatter)).build();
        apiError.log();
        return apiError;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiError handleDataintefrityException(RuntimeException ex) {
        ApiError apiError = ApiError.builder()
            .errors(Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()))
            .message(ex.getMessage())
            .status(HttpStatus.CONFLICT.toString())
            .timestamp(LocalDateTime.now().format(formatter)).build();
        apiError.log();
        return apiError;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError handleOthersExceptions(Throwable ex) {
        ApiError apiError = ApiError.builder()
            .errors(Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()))
            .message(ex.getMessage())
            .status(HttpStatus.NOT_FOUND.toString())
            .timestamp(LocalDateTime.now().format(formatter)).build();
        apiError.log();
        return apiError;
    }
}
