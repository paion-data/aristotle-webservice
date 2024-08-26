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
     * @return a result object indicating failure with BAD_METHOD status
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException e,
                                                            final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestUri, e.getMethod());
        return Result.fail(HttpStatus.BAD_METHOD, e.getMessage());
    }

    /**
     * Handles MissingPathVariableException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a result object indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public Result<Void> handleMissingPathVariableException(final MissingPathVariableException e,
                                                           final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestUri, e);
        return Result.fail(HttpStatus.BAD_REQUEST, String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * Handles MethodArgumentTypeMismatchException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a result object indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e,
                                                                  final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestUri, e);
        return Result.fail(HttpStatus.BAD_REQUEST, String.format(
                "请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(),
                Objects.isNull(e.getRequiredType()) ? "None" : e.getRequiredType().getName(), e.getValue()));
    }

    /**
     * Handles RuntimeException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a result object indicating failure with ERROR status
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(final RuntimeException e, final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("请求地址'{}', 发生未知异常.", requestUri, e);
        return Result.fail(HttpStatus.ERROR, e.getMessage());
    }

    /**
     * Handles Exception.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a result object indicating failure with ERROR status
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(final Exception e, final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestUri, e);
        return Result.fail(HttpStatus.ERROR, e.getMessage());
    }

    /**
     * Handles ValidationException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a result object indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(ValidationException.class)
    public Result<Void> handleValidationException(final ValidationException e, final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("校验失败'{}', 发生系统异常.", requestUri, e);
        return Result.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * Handles ConstraintViolationException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a result object indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(final ConstraintViolationException e,
                                                           final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("校验失败'{}',发生系统异常.", requestUri, e);
        return Result.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * Handles DuplicateKeyException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a result object indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKeyException(final DuplicateKeyException e, final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("数据重复'{}',发生系统异常.", requestUri, e);
        return Result.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * Handles BindException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a result object indicating failure with BAD_REQUEST status
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(final BindException e, final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        log.error("自定义验证失败'{}',发生系统异常.", requestUri, e);
        final String message = e.getAllErrors().get(0).getDefaultMessage();
        return Result.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Handles ServletException.
     *
     * @param e         the exception
     * @param request   the HTTP request
     * @return a result object indicating failure with appropriate status
     */
    @ExceptionHandler(ServletException.class)
    public Result<Void> handleServletException(final ServletException e, final HttpServletRequest request) {
        final Throwable cause = e.getCause();
        final String requestUri = request.getRequestURI();
        if (cause instanceof NoClassDefFoundError || cause instanceof ExceptionInInitializerError) {
            log.error("未启用服务'{}',发生系统异常.", requestUri, e);
            return Result.fail(HttpStatus.ERROR, "未启用服务");
        }
        if (e instanceof NoHandlerFoundException) {
            log.error("未找到路径或资源'{}',发生系统异常.", requestUri, e);
            return Result.fail(HttpStatus.NOT_FOUND, "未找到路径或资源");
        }
        log.error("请求地址'{}',发生未知异常.", requestUri, e);
        return Result.fail(HttpStatus.ERROR, e.getMessage());
    }

    /**
     * Handles CustomizeReturnException.
     *
     * @param e         the exception
     * @return a result object indicating failure with the exception's return code
     */
    @ExceptionHandler(CustomizeReturnException.class)
    public Result<String> handleCustomizeReturnException(final CustomizeReturnException e) {
        log.error(e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg(), e);
        final int code = e.getReturnCode().getCode();
        final String msg = e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg();
        return Result.fail(code, msg);
    }

    /**
     * Handles RuntimeException.
     *
     * @param e         the exception
     * @return a result object indicating failure with INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<List<String>> parameterExceptionHandler(MethodArgumentNotValidException e) {
        // get exception information
        BindingResult exceptions = e.getBindingResult();
        // all error arguments are listed here, returned using list
        List<String> fieldErrorMsg = new ArrayList<>();
        // check whether error information exists in the exception. If yes, use the information in the exception
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                errors.forEach(msg -> fieldErrorMsg.add(msg.getDefaultMessage()));
                return Result.fail(Message.PARAM_VERIFY_FAIL, fieldErrorMsg);
            }
        }
        fieldErrorMsg.add(Message.UNKNOWN_EXCEPTION);
        return Result.fail(Message.PARAM_VERIFY_FAIL, fieldErrorMsg);
    }
}
