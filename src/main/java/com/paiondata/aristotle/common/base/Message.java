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
    public static final String USER_NULL = "The user with OIDCID '%s' does not exist";

    /**
     * The graph does not exist.
     */
    public static final String GRAPH_NULL = "The graph with UUID '%s' does not exist";

    /**
     * The node does not exist.
     */
    public static final String NODE_NULL = "The node with UUID '%s' does not exist";

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
     * The OIDC ID already exists.
     */
    public static final String OIDCID_EXISTS = "The user with OIDC ID '%s' already exists";

    /**
     * The request parameter verification failed.
     */
    public static final String PARAM_VERIFY_FAIL = "Request parameter verification error: ";

    /**
     * Unknown exception.
     */
    public static final String UNKNOWN_EXCEPTION = "Unknown exception.";

    /**
     * OIDC ID must not be blank.
     */
    public static final String OIDCID_MUST_NOT_BE_BLANK = "oidcid must not be blank!";

    /**
     * OIDC IDs must not be empty.
     */
    public static final String OIDCIDS_MUST_NOT_EMPTY = "oidcids must not be empty!";

    /**
     * UUID must not be blank.
     */
    public static final String UUID_MUST_NOT_BE_BLANK = "uuid must not be blank!";

    /**
     * UUIDs must not be empty.
     */
    public static final String UUIDS_MUST_NOT_EMPTY = "uuids must not be empty!";

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
     * Name must not be blank.
     */
    public static final String NAME_MUST_NOT_BE_BLANK = "name must not be blank!";

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
    public static final String DUPLICATE_KEY = "Duplicate key found: %s";

    /**
     * Could not bind node by another graph.
     */
    public static final String BOUND_ANOTHER_GRAPH = "Could not bind node by another graph '%s'";

    /**
     * The relation does not exist.
     */
    public static final String RELATION_NULL = "The relation with uuid '%s' does not exist";

    /**
     * The graph is already bound to another user.
     */
    public static final String GRAPH_BIND_ANOTHER_USER = "The graph with uuid '%s' is already bound to another user";

    /**
     * The node is already bound to another graph.
     */
    public static final String NODE_BIND_ANOTHER_GRAPH = "The node with uuid '%s' is already bound to another graph";

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
    public static final String INPUT_PROPERTIES_ERROR = "The parameter with '%s' is not valid.";

    /**
     * GraphCreateDTO must not be NULL.
     */
    public static final String GRAPH_CREATE_DTO_MUST_NOT_BE_NULL = "GraphCreateDTO must not be NULL!";

    /**
     * fromId must not be blank.
     */
    public static final String FROM_ID_MUST_NOT_BE_BLANK = "fromId must not be blank!";

    /**
     * toId must not be blank.
     */
    public static final String TO_ID_MUST_NOT_BE_BLANK = "toId must not be blank!";

    /**
     * bindDtos must not be empty.
     */
    public static final String BIND_DTOS_MUST_NOT_EMPTY = "bindDtos must not be empty!";
}
