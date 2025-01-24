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
     * Represents the ID field in the database.
     */
    String ID = "id";

    /**
     * Represents the OIDC ID field in the database.
     */
    String OIDCID = "oidcid";

    /**
     * Represents the UUID field in the database.
     */
    String UUID = "uuid";

    /**
     * Represents the graph UUID field in the database.
     */
    String GRAPH_UUID = "graphUuid";

    /**
     * Represents the node UUID field in the database.
     */
    String NODE_UUID = "nodeUuid";

    /**
     * Represents hte relation field in the database.
     */
    String RELATION = "relation";

    /**
     * Represents the relations field in the database.
     */
    String RELATIONS = "relations";

    /**
     * Represents the relation UUID field in the database.
     */
    String RELATION_UUID = "relationUuid";

    /**
     * Represents the source node field in the database.
     */
    String SOURCE_NODE = "sourceNode";

    /**
     * Represents the target node field in the database.
     */
    String TARGET_NODE = "targetNode";

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
     * Represents the current time field in the database.
     */
    String CURRENT_TIME = "currentTime";

    /**
     * Represents the properties field in the database.
     */
    String PROPERTIES = "properties";

    /**
     * Represents the name field in the database.
     */
    String NAME = "name";

    /**
     * Represents the alias for the graph node in Cypher queries.
     * <p>
     * In Cypher queries, aliases are used to assign temporary names to nodes or relationships,
     * which can be referenced in subsequent parts of the query. Using aliases improves the readability
     * and maintainability of the query, avoids ambiguity, and makes referencing easier.
     * <p>
     * Example:
     * <p>
     * MATCH (g:Graph { uuid: $graphUuid })-[:RELATION]->(n:GraphNode { uuid: $nodeUuid})
     * WITH g, n
     * MATCH (n)-[relation:RELATION]-(m:GraphNode)
     * RETURN m, relation
     * In the above query, `g` is the alias for the graph node. The information of the graph node
     * can be retrieved using `record.get(GRAPH_ALISA_G)`.
     */
    String GRAPH_ALISA_G = "g";

    /**
     * Represents the alias for the starting node in Cypher queries.
     * <p>
     * In Cypher queries, aliases are used to assign temporary names to nodes or relationships,
     * which can be referenced in subsequent parts of the query. Using aliases improves the readability
     * and maintainability of the query, avoids ambiguity, and makes referencing easier.
     * <p>
     * Example:
     * <p>
     * MATCH (g:Graph { uuid: $graphUuid })-[:RELATION]->(n:GraphNode { uuid: $nodeUuid})
     * WITH g, n
     * MATCH (n)-[relation:RELATION]-(m:GraphNode)
     * RETURN m, relation
     * In the above query, `n` is the alias for the starting node. The information of the starting node
     * can be retrieved using `record.get(NODE_ALIAS_N)`.
     */
    String NODE_ALIAS_N = "n";

    /**
     * Represents the alias for the neighbor node in Cypher queries.
     * <p>
     * In Cypher queries, aliases are used to assign temporary names to nodes or relationships,
     * which can be referenced in subsequent parts of the query. Using aliases improves the readability
     * and maintainability of the query, avoids ambiguity, and makes referencing easier.
     * <p>
     * Example:
     * <p>
     * MATCH (g:Graph { uuid: $graphUuid })-[:RELATION]->(n:GraphNode { uuid: $nodeUuid})
     * WITH g, n
     * MATCH (n)-[relation:RELATION]-(m:GraphNode)
     * RETURN m, relation
     * In the above query, `m` is the alias for the neighbor node. The information of the neighbor node
     * can be retrieved using `record.get(NODE_ALIAS_M)`.
     */
    String NODE_ALIAS_M = "m";

    /**
     * Represents the nodes in cypher.
     */
    String NODES = "nodes";

    /**
     * Represents the path in cypher.
     */
    String PATH = "path";

    /**
     * Represents the quote in cypher.
     */
    String QUOTE = "'";

    /**
     * Represents the storage unit in caffeine cache config.
     */
    String STORAGE_UNIT_KB = "KB";

    /**
     * Represents the storage unit in caffeine cache config.
     */
    String STORAGE_UNIT_MB = "MB";

    /**
     * Represents the storage unit in caffeine cache config.
     */
    String STORAGE_UNIT_GB = "GB";
}
