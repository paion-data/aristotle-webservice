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
 * Defines common constants used throughout the application.
 */
public interface Constants {

    /**
     * Represents the UTF-8 character encoding.
     */
    String UTF8 = "UTF-8";

    /**
     * Represents the GBK character encoding.
     */
    String GBK = "GBK";

    /**
     * Prefix for web addresses.
     */
    String WWW = "www.";

    /**
     * Prefix for HTTP protocol.
     */
    String HTTP = "http://";

    /**
     * Prefix for HTTPS protocol.
     */
    String HTTPS = "https://";

    /**
     * Indicates a successful operation.
     */
    Integer SUCCESS = HttpStatus.SUCCESS;

    /**
     * Indicates a failed operation.
     */
    Integer FAIL = HttpStatus.ERROR;

    /**
     * Represents the UUID field in the database.
     */
    String UUID = "uuid";

    /**
     * Represents the start node field in the database.
     */
    String START_NODE = "startNode";

    /**
     * Represents the end node field in the database.
     */
    String END_NODE = "endNode";

    /**
     * Represents the title field in the database.
     */
    String TITLE = "title";

    /**
     * Represents the description field in the database.
     */
    String DESCRIPTION = "description";

    /**
     * Represents the update time field in the database.
     */
    String UPDATE_TIME = "updateTime";

    /**
     * Represents the update time field in the database without hump.
     */
    String UPDATE_TIME_WITHOUT_HUMP = "update_time";

    /**
     * Represents the create time field in the database.
     */
    String CREATE_TIME = "createTime";

    /**
     * Represents the create time field in the database without hump.
     */
    String CREATE_TIME_WITHOUT_HUMP = "create_time";

    /**
     * Represents the name field in the database.
     */
    String NAME = "name";
}
