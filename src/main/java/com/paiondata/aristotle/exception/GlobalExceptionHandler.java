/*
 * Copyright 2024 Paion Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paiondata.aristotle.exception;

import com.paiondata.aristotle.common.base.HttpStatus;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.paiondata.aristotle.exception.customize.CustomizeReturnException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Global exception handler for processing exceptions and returning appropriate HTTP responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles HttpRequestMethodNotSupportedException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a response entity indicating failure with BAD_METHOD status
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupported(
            final HttpRequestMethodNotSupportedException e, final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("Request URL '{}', does not support '{}' request", requestUri, e.getMethod());
        return ResponseEntity.status(HttpStatus.BAD_METHOD)
                .body(Result.fail(HttpStatus.BAD_METHOD, e.getMessage()));
    }

    /**
     * Handles MissingPathVariableException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a response entity indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<Result<Void>> handleMissingPathVariableException(final MissingPathVariableException e,
                                                                           final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("The required path variable '{}' is missing in the request path, causing a system exception.",
                requestUri, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(HttpStatus.BAD_REQUEST,
                        String.format("The required path variable [%s] is missing in the request path",
                                e.getVariableName())));
    }

    /**
     * Handles MethodArgumentTypeMismatchException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a response entity indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException e, final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("The request parameter type does not match '{}', causing a system exception.", requestUri, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(HttpStatus.BAD_REQUEST, String.format(
                        "The request parameter type does not match, parameter [%s] requires type '%s', "
                                + "but the input value is '%s'", e.getName(),
                        Objects.isNull(e.getRequiredType()) ? "None" : e.getRequiredType().getName(), e.getValue())));
    }


    /**
     * Handles RuntimeException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a response entity indicating failure with INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Void>> handleRuntimeException(final RuntimeException e,
                                                               final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error(Message.UNKNOWN_EXCEPTION, requestUri, e);
        return ResponseEntity.status(HttpStatus.ERROR)
                .body(Result.fail(HttpStatus.ERROR, e.getMessage()));
    }

    /**
     * Handles Exception.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a response entity indicating failure with INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(final Exception e, final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("Request URL '{}', encountered a system exception.", requestUri, e);
        return ResponseEntity.status(HttpStatus.ERROR)
                .body(Result.fail(HttpStatus.ERROR, e.getMessage()));
    }

    /**
     * Handles ValidationException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a response entity indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Result<Void>> handleValidationException(final ValidationException e,
                                                                  final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("Validation failed '{}', encountered a system exception.", requestUri, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    /**
     * Handles ConstraintViolationException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a response entity indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(final ConstraintViolationException e,
                                                                           final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("Constraint violation failed '{}', encountered a system exception.", requestUri, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    /**
     * Handles DuplicateKeyException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a response entity indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> handleDuplicateKeyException(final DuplicateKeyException e,
                                                                    final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("Data duplication '{}', encountered a system exception.", requestUri, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    /**
     * Handles BindException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a response entity indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(final BindException e, final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("Custom validation failed '{}', encountered a system exception.", requestUri, e);
        final String message = e.getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(HttpStatus.BAD_REQUEST, message));
    }

    /**
     * Handles ServletException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a response entity indicating failure with appropriate status
     */
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<Result<Void>> handleServletException(final ServletException e,
                                                               final HttpServletRequest request) {
        final Throwable cause = e.getCause();
        final String requestUri = request.getRequestURI();
        if (cause instanceof NoClassDefFoundError || cause instanceof ExceptionInInitializerError) {
            log.error("Service not enabled '{}', encountered a system exception.", requestUri, e);
            return ResponseEntity.status(HttpStatus.ERROR)
                    .body(Result.fail(HttpStatus.ERROR, "Service not enabled"));
        }
        if (e instanceof NoHandlerFoundException) {
            log.error("Path or resource not found '{}', encountered a system exception.", requestUri, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.fail(HttpStatus.NOT_FOUND, "Path or resource not found"));
        }
        log.error(Message.UNKNOWN_EXCEPTION, requestUri, e);
        return ResponseEntity.status(HttpStatus.ERROR)
                .body(Result.fail(HttpStatus.ERROR, e.getMessage()));
    }

    /**
     * Handles CustomizeReturnException.
     *
     * @param e         the exception
     * @return a response entity indicating failure with the exception's return code
     */
    @ExceptionHandler(CustomizeReturnException.class)
    public ResponseEntity<Result<String>> handleCustomizeReturnException(final CustomizeReturnException e) {
        log.error(e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg(), e);
        final int code = e.getReturnCode().getCode();
        final String msg = e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg();
        return ResponseEntity.status(code)
                .body(Result.fail(code, msg));
    }

    /**
     * Handles MethodArgumentNotValidException.
     *
     * @param e         the exception
     * @return a response entity indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<List<String>>> parameterExceptionHandler(final MethodArgumentNotValidException e) {
        // get exception information
        final BindingResult exceptions = e.getBindingResult();
        // all error arguments are listed here, returned using list
        final List<String> fieldErrorMsg = new ArrayList<>();
        // check whether error information exists in the exception. If yes, use the information in the exception
        if (exceptions.hasErrors()) {
            final List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                errors.forEach(msg -> fieldErrorMsg.add(msg.getDefaultMessage()));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Result.fail(HttpStatus.BAD_REQUEST, Message.PARAM_VERIFY_FAIL + fieldErrorMsg));
            }
        }
        fieldErrorMsg.add("Unknown exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(HttpStatus.BAD_REQUEST, Message.PARAM_VERIFY_FAIL + fieldErrorMsg));
    }
}
