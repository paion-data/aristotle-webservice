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

public class Message {
    public static final String USER_NULL = "The user does not exist: ";
    public static final String GRAPH_NULL = "The graph does not exist: ";
    public static final String GRAPH_NODE_NULL = "The graph node does not exist: ";
    public static final String CREATE_SUCCESS = "Created successfully.";
    public static final String UPDATE_SUCCESS = "Updated successfully.";
    public static final String BOUND_SUCCESS = "Bound successfully.";
    public static final String UIDCID_EXISTS = "UIDCID already exists: ";
    public static final String PARAM_VERIFY_FAIL = "Request parameter verification error: ";
    public static final String UNKNOWN_EXCEPTION = "Unknown exception.";
    public static final String UIDCID_MUST_NOT_BE_BLANK = "uidcid must not be blank!";
    public static final String UUID_MUST_NOT_BE_BLANK = "uuid must not be blank!";
    public static final String RELATION_MUST_NOT_BE_BLANK = "relation must not be blank!";
    public static final String NICK_NAME_MUST_NOT_BE_BLANK = "nickName must not be blank!";
    public static final String TITLE_MUST_NOT_BE_BLANK = "title must not be blank!";
    public static final String DESCRIPTION_MUST_NOT_BE_BLANK = "description must not be blank!";
    public static final String TEMPORARY_ID_MUST_NOT_NULL = "temporaryId must not null!";
    public static final String DELETE_SUCCESS = "Deleted successfully.";
    public static final String DUPLICATE_KEY = "Duplicate key found: ";
    public static final String BOUND_ANOTHER_GRAPH = "Could not bind graphNode by another graph: ";
    public static final String GRAPH_NODE_RELATION_NULL = "The relation does not exist: ";
}
