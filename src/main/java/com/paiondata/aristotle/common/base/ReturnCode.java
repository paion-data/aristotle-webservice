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

import lombok.Getter;

/**
 * Enum representing different return codes for various operations.
 */
@Getter
public enum ReturnCode {

    /**
     * Indicates a successful operation.
     */
    SUCCESS(HttpStatus.SUCCESS, "Successful operation"),

    /**
     * Indicates a failed operation.
     */
    FAIL(HttpStatus.ERROR, "Operation failure"),

    /**
     * Indicates a warning.
     */
    WARN(HttpStatus.WARN, "System warning"),

    /**
     * Indicates that a required request parameter is missing.
     */
    REQUEST_REQUIRED_PARAMETER_IS_EMPTY(13001, "The required request parameter is missing"),

    /**
     * Indicates that a parameter format mismatch occurred.
     */
    PARAMETER_FORMAT_MISMATCH(13002, "The parameter format mismatch occurred"),

    /**
     * Indicates that too many requests have been made.
     */
    TOO_MANY_REQUESTS(13003, "Too many requests have been made"),

    /**
     * Indicates an error occurred in the database service.
     */
    ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE(20000, "An error occurred in the database service."),

    /**
     * Indicates an error occurred in the message queue (MQ) service.
     */
    MQ_SERVICE_ERROR(30000, "An error occurred in the message queue (MQ) service"),

    /**
     * Indicates an error occurred in the main memory database service.
     */
    MAIN_MEMORY_DATABASE_SERVICE_ERROR(30001, "An error occurred in the main memory database service");

    /**
     * The numeric code associated with the return code.
     */
    private final int code;

    /**
     * The message associated with the return code.
     */
    private final String msg;

    /**
     * Constructs a new return code with the specified code and message.
     *
     * @param code  the numeric code
     * @param msg   the message
     */
    ReturnCode(final int code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * Returns the numeric code associated with this return code.
     *
     * @return the numeric code
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the message associated with this return code.
     *
     * @return the message
     */
    public String getMsg() {
        return msg;
    }
}
