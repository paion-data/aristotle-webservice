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

/**
 * Defines standard HTTP status codes.
 */
public interface HttpStatus {

    /**
     * Indicates the request has been fulfilled and the result is returned in the response.
     *
     * @param SUCCESS The status code for a successful request.
     */
    int SUCCESS = 200;

    /**
     * Indicates that the request has been fulfilled and resulted in the creation of a resource.
     *
     * @param CREATED The status code for a successfully created resource.
     */

    int CREATED = 201;

    /**
     * Indicates that the request has been accepted for processing, but the processing has not been completed.
     *
     * @param ACCEPTED The status code for an accepted request.
     */
    int ACCEPTED = 202;

    /**
     * Indicates the server has fulfilled the request and there is no additional content to send in the response body.
     *
     * @param NO_CONTENT The status code for a request with no content.
     */
    int NO_CONTENT = 204;

    /**
     * Indicates that the requested page has moved permanently to a new URL.
     *
     * @param MOVED_PERM The status code for a permanently moved resource.
     */
    int MOVED_PERM = 301;

    /**
     * Indicates that the response to the request can be found under another URI.
     *
     * @param SEE_OTHER The status code for a redirect to another URI.
     */
    int SEE_OTHER = 303;

    /**
     * Indicates that the requested resource has not been modified.
     *
     * @param NOT_MODIFIED The status code for an unmodified resource.
     */
    int NOT_MODIFIED = 304;

    /**
     * Indicates the server cannot or will not process the request due to something that is perceived to be a error.
     *
     * @param BAD_REQUEST The status code for a bad request.
     */
    int BAD_REQUEST = 400;

    /**
     * Indicates that the server has not found anything matching the Request-URI.
     *
     * @param NOT_FOUND The status code for a not found resource.
     */
    int NOT_FOUND = 404;

    /**
     * Indicates the method specified in the Request-Line is not allowed for the resource identified by Request-URI.
     *
     * @param BAD_METHOD The status code for an unsupported HTTP method.
     */
    int BAD_METHOD = 405;

    /**
     * Indicates that the request could not be completed due to a conflict with the current state of the resource.
     *
     * @param CONFLICT The status code for a conflicting request.
     */
    int CONFLICT = 409;

    /**
     * Indicates that the server does not support the media type of the request entity.
     *
     * @param UNSUPPORTED_TYPE The status code for an unsupported media type.
     */
    int UNSUPPORTED_TYPE = 415;

    /**
     * Indicates that the server encountered an unexpected condition that prevented it from fulfilling the request.
     *
     * @param ERROR The status code for a server error.
     */
    int ERROR = 500;

    /**
     * Indicates that the server does not support the functionality required to fulfill the request.
     *
     * @param NOT_IMPLEMENTED The status code for a not implemented request.
     */
    int NOT_IMPLEMENTED = 501;

    /**
     * Indicates a warning about the response.
     *
     * @param WARN The status code for a warning.
     */
    int WARN = 600;
}
