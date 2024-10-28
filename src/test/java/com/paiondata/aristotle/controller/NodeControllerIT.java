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
package com.paiondata.aristotle.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.paiondata.aristotle.common.base.TestConstants;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * NodeControllerIT is a test class that extends AbstractIT and contains integration tests for the
 * NodeController. These tests validate various operations such as creating, updating, deleting, and retrieving
 * nodes and their relationships.
 */
@Transactional(propagation = Propagation.NEVER)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NodeControllerIT extends AbstractIT {

    /**
     * The embedded Neo4j database server used for testing.
     */
    private static Neo4j embeddedDatabaseServer;

    /**
     * A static string variable to store the UUID of a created graph.
     */
    private static String graphUuid;

    /**
     * A static string variable to store the UUID of the first created node.
     */
    private static String nodeUuid1;

    /**
     * A static string variable to store the UUID of the second created node.
     */
    private static String nodeUuid2;

    /**
     * A static string variable to store the UUID of the third created node.
     */
    private static String nodeUuid3;

    /**
     * Sets up the embedded Neo4j database server before all tests.
     */
    @BeforeAll
    static void setUp() {
        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .build();
    }

    /**
     * Stops the embedded Neo4j database server after all tests.
     */
    @AfterAll
    static void stop() {
        if (embeddedDatabaseServer != null) {
            embeddedDatabaseServer.close();
        }
    }

    /**
     * Registers dynamic properties for the Neo4j database connection.
     *
     * @param registry The dynamic property registry.
     */
    @DynamicPropertySource
    static void neo4jProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", embeddedDatabaseServer::boltURI);
    }

    /**
     * Parameterized test to verify if the JSON API correctly handles invalid user creation requests by returning
     * a 400 Bad Request status code and appropriate error messages.
     *
     * @param oidcid The OIDC ID to be used in the request body.
     * @param title The title to be used in the request body.
     * @param description The description to be used in the request body.
     * @param temporaryId1 The first temporary ID to be used in the request body.
     * @param temporaryId2 The second temporary ID to be used in the request body.
     * @param fromId The from ID to be used in the request body.
     * @param relationName The relation name to be used in the request body.
     * @param toId The to ID to be used in the request body.
     * @param expectedError The expected error message.
     */
    @ParameterizedTest
    @CsvSource({
            "'', title, description, id1, id2, id1, relation, id2, oidcid must not be blank!",
            "oidcid, '', description, id1, id2, id1, relation, id2, title must not be blank!",
            "oidcid, title, '', id1, id2, id1, relation, id2, description must not be blank!",
            "oidcid, title, description, '', id2, id1, relation, id2, temporaryId must not null!",
            "oidcid, title, description, id1, '', id1, relation, id2, temporaryId must not null!",
            "oidcid, title, description, id1, id2, '', relation, id2, fromId must not be blank!",
            "oidcid, title, description, id1, id2, id1, '', id2, relation must not be blank!",
            "oidcid, title, description, id1, id2, id1, relation, '', toId must not be blank!"})
    @Order(1)
    void jsonApiHandlesInvalidUserCreationRequests(final String oidcid, final String title, final String description,
                                                   final String temporaryId1, final String temporaryId2,
                                                   final String fromId, final String relationName, final String toId,
                                                   final String expectedError) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-graph-nodes-to-valid.json"), oidcid, title, description,
                        temporaryId1, temporaryId2, fromId, relationName, toId))
                .when()
                .post(NODE_ENDPOINT + "/graph")
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        assertEquals(expectedError, response.jsonPath().get(TestConstants.DATA_0));
    }

    /**
     * Parameterized test to verify if the JSON API correctly handles invalid node creation requests by returning a
     * 400 Bad Request status code and appropriate error messages.
     *
     * @param uuid The graph UUID to be used in the request body.
     * @param temporaryId1 The first temporary ID to be used in the request body.
     * @param temporaryId2 The second temporary ID to be used in the request body.
     * @param fromId The from ID to be used in the request body.
     * @param relationName The relation name to be used in the request body.
     * @param toId The to ID to be used in the request body.
     * @param expectedError1 The first expected error message.
     * @param expectedError2 The second expected error message.
     */
    @ParameterizedTest
    @CsvSource({
            "'', id1, id2, id1, relation, id2, uuid must not be blank!, ''",
            "id, '', id2, id1, relation, id2, temporaryId must not null!, ''",
            "'', id1, id2, id1, relation, id2, uuid must not be blank!, ''",
            "id, id1, id2, id1, '', id2, relation must not be blank!, ''",
            "'', '', id2, id1, relation, id2, uuid must not be blank!, temporaryId must not null!"})
    @Order(2)
    void jsonApiHandlesInvalidNodeCreationRequests(final String uuid, final String temporaryId1,
                                                   final String temporaryId2, final String fromId,
                                                   final String relationName, final String toId,
                                                   final String expectedError1, final String expectedError2) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-nodes-to-valid.json"), uuid,
                        temporaryId1, temporaryId2, fromId, relationName, toId))
                .when()
                .post(NODE_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        final List<String> actualData = response.jsonPath().getList(TestConstants.DATA);
        final List<String> expectedData = Arrays.stream(new String[]{expectedError1, expectedError2})
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Collections.sort(actualData);
        Collections.sort(expectedData);

        assertEquals(expectedData, actualData);
    }

    /**
     * Parameterized test to verify if the JSON API correctly handles invalid node updating requests by returning a
     * 400 Bad Request status code and appropriate error messages.
     *
     * @param fromId The from ID to be used in the request body.
     * @param relationName The relation name to be used in the request body.
     * @param toId The to ID to be used in the request body.
     * @param expectedMsg The expected error message.
     */
    @ParameterizedTest
    @CsvSource({
            "'', relation, id2, bindNodes.dtos[0].fromId: fromId must not be blank!",
            "id1, '', id2, bindNodes.dtos[0].relationName: relation must not be blank!",
            "id1, relation, '', bindNodes.dtos[0].toId: toId must not be blank!"})
    @Order(3)
    void jsonApiHandlesInvalidNodeUpdatingRequests(final String fromId, final String relationName, final String toId,
                                                   final String expectedMsg) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("relate-node-to-valid.json"), fromId, relationName, toId))
                .when()
                .post(NODE_ENDPOINT + "/bind")
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        assertEquals(expectedMsg, response.jsonPath().get(TestConstants.MSG));
    }

    /**
     * Tests if the JSON API correctly handles invalid node updating requests by returning a 400 Bad Request status code
     * and appropriate error messages.
     */
    @Test
    @Order(4)
    void jsonApiHandlesInvalidNodeUpdatingRequests() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_NODE_JSON), "", ""))
                .when()
                .post(NODE_ENDPOINT + UPDATE_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        assertEquals("Request parameter verification error: ", response.jsonPath().get(TestConstants.MSG));
        assertEquals("uuid must not be blank!", response.jsonPath().get(TestConstants.DATA_0));
    }

    /**
     * Tests if the database is initially empty by making a GET request to the user endpoint and verifying that the
     * response data is empty.
     */
    @Test
    @Order(5)
    void databaseIsEmpty() {
        final Response response = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT);
        response.then()
                .statusCode(HttpStatus.OK.value());

        final List<String> data = response.jsonPath().getList(TestConstants.DATA);
        assertTrue(data.isEmpty());
    }

    /**
     * Tests if a user entity can be posted via the JSON API by making a POST request to the user endpoint and
     * verifying the response.
     */
    @Test
    @Order(6)
    void anUserEntityIsPostedViaJsonApi() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_UPDATE_USER_JSON), TestConstants.TEST_ID1, TestConstants.TEST_NAME1))
                .when()
                .post(USER_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(TestConstants.TEST_ID1, response.jsonPath().get(TestConstants.DATA_OIDCID));
        assertEquals(TestConstants.TEST_NAME1, response.jsonPath().get(TestConstants.DATA_NICKNAME));
    }

    /**
     * Tests if a graph entity can be posted via the JSON API without any nodes by making a POST request to the
     * node/graph endpoint and verifying the response.
     */
    @Test
    @Order(7)
    void anGraphEntityIsPostedViaJsonApiWithoutAnyNode() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-graph.json"), TestConstants.TEST_ID1, TestConstants.TEST_TITLE1))
                .when()
                .post(NODE_ENDPOINT + GRAPH_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        graphUuid = response.jsonPath().get(TestConstants.DATA_UUID);

        assertNotNull(response.jsonPath().get(TestConstants.DATA_UUID));
        assertEquals(TestConstants.TEST_TITLE1, response.jsonPath().get("data.title"));
    }

    /**
     * Tests if a node entity can be posted via the JSON API by making a POST request to the node endpoint and
     * verifying the response.
     */
    @Test
    @Order(8)
    void nodeEntityIsPostedViaJsonApi() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-node.json"), graphUuid,
                        TestConstants.TEST_TITLE1, TestConstants.TEST_TITLE2, TestConstants.TEST_TITLE3))
                .when()
                .post(NODE_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        nodeUuid1 = response.jsonPath().get(TestConstants.DATA_0_UUID);
        nodeUuid2 = response.jsonPath().get(TestConstants.DATA_1_UUID);
        nodeUuid3 = response.jsonPath().get(TestConstants.DATA_2_UUID);

        assertNotNull(response.jsonPath().get(TestConstants.DATA_0_UUID));
        assertNotNull(response.jsonPath().get(TestConstants.DATA_1_UUID));
        assertNotNull(response.jsonPath().get(TestConstants.DATA_2_UUID));
    }

    /**
     * Tests if a node entity can be retrieved by making a POST request to the graph endpoint
     * and verifying the response.
     */
    @Test
    @Order(9)
    void weCanGetThatNodeEntityNext() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON), graphUuid))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        final List<String> expectedData = Arrays.asList(TestConstants.TEST_TITLE1,
                TestConstants.TEST_TITLE2, TestConstants.TEST_TITLE3);

        final List<String> actualData = response.jsonPath().getList("data.nodes.properties.title");

        assertTrue(actualData.containsAll(expectedData));
    }

    /**
     * Tests if a node entity can be updated by making a POST request to the node/update endpoint
     * and verifying the response.
     */
    @Test
    @Order(10)
    void weCanUpdateThatNodeEntity() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_NODE_JSON), nodeUuid1, TestConstants.TEST_TITLE4))
                .when()
                .post(NODE_ENDPOINT + UPDATE_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * Tests if a node entity with updated attributes can be retrieved by making a GET request to the node endpoint
     * and verifying the response.
     */
    @Test
    @Order(11)
    void weCanGetThatNodeEntityWithUpdatedAttribute() {
        final Response response = RestAssured
                .given()
                .when()
                .get(NODE_ENDPOINT + TestConstants.SLASH + nodeUuid1)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(nodeUuid1, response.jsonPath().get(TestConstants.DATA_UUID));
        assertEquals(TestConstants.TEST_TITLE4, response.jsonPath().get("data.properties.title"));
    }

    /**
     * Tests if nodes can be related by making a POST request to the node/bind endpoint and verifying the response.
     */
    @Test
    @Order(12)
    void theNodesAreRelated() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("relate-node.json"), nodeUuid1, nodeUuid2, nodeUuid1,
                        nodeUuid3, nodeUuid2, nodeUuid3))
                .when()
                .post("/node/bind")
                .then()
                .extract()
                .response();
    }

    /**
     * Tests if the relations of nodes can be retrieved via the graph UUID by making a POST request to the
     * graph endpoint and verifying the response.
     */
    @Test
    @Order(13)
    void weCanGetTheRelationOfNodesViaGraphUuid() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON), graphUuid))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response();

        assertEquals("-", response.jsonPath().get("data.relations[0].name"));
    }

    /**
     * Tests if a node entity can be deleted by making a DELETE request to the node endpoint and verifying the response.
     */
    @Test
    @Order(14)
    void weCanDeleteThatNodeEntity() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("delete-node.json"), graphUuid, nodeUuid1))
                .when()
                .delete(NODE_ENDPOINT);
        response.then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * Tests if a deleted node entity is no longer found in the database by making a GET request to the node endpoint
     * and verifying the response.
     */
    @Test
    @Order(15)
    void thatNodeEntityIsNotFoundInDatabaseAnymore() {
        final Response response = RestAssured
                .given()
                .when()
                .get(NODE_ENDPOINT + TestConstants.SLASH + nodeUuid1);
        response.then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        assertNull(response.jsonPath().get(TestConstants.DATA));
    }
}
