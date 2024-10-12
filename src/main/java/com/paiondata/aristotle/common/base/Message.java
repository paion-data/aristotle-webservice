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
 * This class contains various message constants used throughout the application.
 */
public class Message {

    /**
     * The user does not exist.
     */
    public static final String USER_NULL = "The user does not exist: ";

    /**
     * The graph does not exist.
     */
    public static final String GRAPH_NULL = "The graph does not exist: ";

    /**
     * The graph node does not exist.
     */
    public static final String GRAPH_NODE_NULL = "The graph node does not exist: ";

    /**
     * The operation succeeded.
     */
    public static final String CREATE_SUCCESS = "Created successfully.";

    /**
     * The operation succeeded.
     */
    public static final String UPDATE_SUCCESS = "Updated successfully.";

    /**
     * The operation succeeded.
     */
    public static final String BOUND_SUCCESS = "Bound successfully.";

    /**
     * The UIDCID already exists.
     */
    public static final String UIDCID_EXISTS = "UIDCID already exists: ";

    /**
     * The request parameter verification failed.
     */
    public static final String PARAM_VERIFY_FAIL = "Request parameter verification error: ";

    /**
     * Unknown exception.
     */
    public static final String UNKNOWN_EXCEPTION = "Unknown exception.";

    /**
     * UIDCID must not be blank.
     */
    public static final String UIDCID_MUST_NOT_BE_BLANK = "uidcid must not be blank!";

    /**
     * UUID must not be blank.
     */
    public static final String UUID_MUST_NOT_BE_BLANK = "uuid must not be blank!";

    /**
     * Relation must not be blank.
     */
    public static final String RELATION_MUST_NOT_BE_BLANK = "relation must not be blank!";

    /**
     * NickName must not be blank.
     */
    public static final String NICK_NAME_MUST_NOT_BE_BLANK = "nickName must not be blank!";

    /**
     * Title must not be blank.
     */
    public static final String TITLE_MUST_NOT_BE_BLANK = "title must not be blank!";

    /**
     * Description must not be blank.
     */
    public static final String DESCRIPTION_MUST_NOT_BE_BLANK = "description must not be blank!";

    /**
     * TemporaryId must not null.
     */
    public static final String TEMPORARY_ID_MUST_NOT_NULL = "temporaryId must not null!";

    /**
     * Delete succeeded.
     */
    public static final String DELETE_SUCCESS = "Deleted successfully.";

    /**
     * Duplicate key found.
     */
    public static final String DUPLICATE_KEY = "Duplicate key found: ";

    /**
     * Could not bind graphNode by another graph.
     */
    public static final String BOUND_ANOTHER_GRAPH = "Could not bind graphNode by another graph: ";

    /**
     * The relation does not exist.
     */
    public static final String GRAPH_NODE_RELATION_NULL = "The relation does not exist: ";

    /**
     * The graph is already bound to another user.
     */
    public static final String GRAPH_BIND_ANOTHER_USER = "The graph is already bound to another user: ";

    /**
     * The node is already bound to another graph.
     */
    public static final String NODE_BIND_ANOTHER_USER = "The node is already bound to another graph: ";

    /**
     * Transaction is null.
     */
    public static final String TRANSACTION_NULL = "Transaction is null.";

    /**
     * Method does not have a Transaction parameter.
     */
    public static final String METHOD_WITHOUT_TRANSACTION = "Method does not have a Transaction parameter.";

    /**
     * Input properties error.
     */
    public static final String INPUT_PROPERTIES_ERROR = "This parameter cannot be passed: ";
}
