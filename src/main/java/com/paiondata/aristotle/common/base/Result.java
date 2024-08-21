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
package com.paiondata.aristotle.common.base;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Represents a generic result object that encapsulates the outcome of an operation.
 * It includes a status code, a message, and optional data.
 *
 * @param <T>  the type of the data
 */
@Data
@NoArgsConstructor
public class Result<T> implements Serializable {

    /**
     * Represents a successful operation.
     */
    public static final int SUCCESS = ReturnCode.SUCCESS.getCode();

    /**
     * Message associated with a successful operation.
     */
    public static final String SUCCESS_MSG = ReturnCode.SUCCESS.getMsg();

    /**
     * Represents a failed operation.
     */
    public static final int FAIL = ReturnCode.FAIL.getCode();

    /**
     * Message associated with a failed operation.
     */
    public static final String FAIL_MSG = ReturnCode.FAIL.getMsg();

    /**
     * Represents a warning.
     */
    public static final int WARN = ReturnCode.WARN.getCode();

    /**
     * Message associated with a warning.
     */
    public static final String WARN_MSG = ReturnCode.WARN.getMsg();

    /**
     * Serialization version number.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The status code of the result.
     */
    private int code;

    /**
     * A message describing the result.
     */
    private String msg;

    /**
     * Optional data associated with the result.
     */
    private T data;

    /**
     * Creates a new result instance.
     *
     * @param data  the data associated with the result
     * @param code  the status code
     * @param msg   the message
     * @param <T>   the type of the data
     * @return      a new result instance
     */
    private static <T> Result<T> restResult(final T data, final int code, final String msg) {
        final Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    /**
     * Determines if the given result represents an error.
     *
     * @param ret   the result to check
     * @param <T>   the type of the data
     * @return      true if the result is an error, false otherwise
     */
    public static <T> Boolean isError(final Result<T> ret) {
        return FAIL == ret.getCode();
    }

    /**
     * Determines if the given result represents a success.
     *
     * @param ret   the result to check
     * @param <T>   the type of the data
     * @return      true if the result is a success, false otherwise
     */
    public static <T> Boolean isSuccess(final Result<T> ret) {
        return SUCCESS == ret.getCode();
    }

    /**
     * Creates a successful result without data.
     *
     * @param <T>   the type of the data
     * @return      a successful result
     */
    public static <T> Result<T> ok() {
        return restResult(null, SUCCESS, SUCCESS_MSG);
    }

    /**
     * Creates a successful result with data.
     *
     * @param data  the data associated with the result
     * @param <T>   the type of the data
     * @return      a successful result with data
     */
    public static <T> Result<T> ok(final T data) {
        return restResult(data, SUCCESS, SUCCESS_MSG);
    }

    /**
     * Creates a successful result with a custom message.
     *
     * @param msg   the custom message
     * @param <T>   the type of the data
     * @return      a successful result with a custom message
     */
    public static <T> Result<T> ok(final String msg) {
        return restResult(null, SUCCESS, msg);
    }

    /**
     * Creates a successful result with data and a custom message.
     *
     * @param msg   the custom message
     * @param data  the data associated with the result
     * @param <T>   the type of the data
     * @return      a successful result with data and a custom message
     */
    public static <T> Result<T> ok(final String msg, final T data) {
        return restResult(data, SUCCESS, msg);
    }

    /**
     * Creates a failed result without data.
     *
     * @param <T>   the type of the data
     * @return      a failed result
     */
    public static <T> Result<T> fail() {
        return restResult(null, FAIL, FAIL_MSG);
    }

    /**
     * Creates a failed result with a custom message.
     *
     * @param msg   the custom message
     * @param <T>   the type of the data
     * @return      a failed result with a custom message
     */
    public static <T> Result<T> fail(final String msg) {
        return restResult(null, FAIL, msg);
    }

    /**
     * Creates a failed result with data.
     *
     * @param data  the data associated with the result
     * @param <T>   the type of the data
     * @return      a failed result with data
     */
    public static <T> Result<T> fail(final T data) {
        return restResult(data, FAIL, FAIL_MSG);
    }

    /**
     * Creates a failed result with data and a custom message.
     *
     * @param msg   the custom message
     * @param data  the data associated with the result
     * @param <T>   the type of the data
     * @return      a failed result with data and a custom message
     */
    public static <T> Result<T> fail(final String msg, final T data) {
        return restResult(data, FAIL, msg);
    }

    /**
     * Creates a failed result with a custom code and message.
     *
     * @param code  the custom code
     * @param msg   the custom message
     * @param <T>   the type of the data
     * @return      a failed result with a custom code and message
     */
    public static <T> Result<T> fail(final int code, final String msg) {
        return restResult(null, code, msg);
    }

    /**
     * Creates a failed result using a predefined return code.
     *
     * @param returnCode  the predefined return code
     * @param <T>         the type of the data
     * @return            a failed result with a predefined return code
     */
    public static <T> Result<T> fail(final ReturnCode returnCode) {
        return restResult(null, returnCode.getCode(), returnCode.getMsg());
    }

    /**
     * Creates a failed result with data and a predefined return code.
     *
     * @param data        the data associated with the result
     * @param returnCode  the predefined return code
     * @param <T>         the type of the data
     * @return            a failed result with data and a predefined return code
     */
    public static <T> Result<T> fail(final T data, final ReturnCode returnCode) {
        return restResult(data, returnCode.getCode(), returnCode.getMsg());
    }

    /**
     * Creates a warning result without data.
     *
     * @param <T>   the type of the data
     * @return      a warning result
     */
    public static <T> Result<T> warn() {
        return restResult(null, WARN, WARN_MSG);
    }

    /**
     * Creates a warning result with a custom message.
     *
     * @param msg   the custom message
     * @param <T>   the type of the data
     * @return      a warning result with a custom message
     */
    public static <T> Result<T> warn(final String msg) {
        return restResult(null, WARN, msg);
    }

    /**
     * Creates a warning result with data and a custom message.
     *
     * @param msg   the custom message
     * @param data  the data associated with the result
     * @param <T>   the type of the data
     * @return      a warning result with data and a custom message
     */
    public static <T> Result<T> warn(final String msg, final T data) {
        return restResult(data, WARN, msg);
    }

    /**
     * Creates an empty result.
     *
     * @param <T>   the type of the data
     * @return      an empty result
     */
    public static <T> Result<T> empty() {
        return null;
    }
}
