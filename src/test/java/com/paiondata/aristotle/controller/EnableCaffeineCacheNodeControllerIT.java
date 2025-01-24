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

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.TestConstants;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EnableCaffeineCacheNodeControllerIT is a test class that extends AbstractIT and contains integration tests for the
 * NodeController. These tests validate various operations such as creating, updating, deleting, and retrieving
 * nodes and their relationships.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnableCaffeineCacheNodeControllerIT extends AbstractEnableCaffeineCacheIT {

    /**
     * A static string variable to store the UUID of a first created graph.
     */
    private static String graphUuid1;

    /**
     * A static string variable to store the UUID of a second created graph.
     */
    private static String graphUuid2;

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
    @Order(1)
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
     * Tests if the JSON API correctly handles invalid node updating requests by returning a 400 Bad Request status code
     * and appropriate error messages.
     */
    @Test
    @Order(2)
    void jsonApiHandlesInvalidNodeUpdatingRequests() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_NODE_JSON), "", "", ""))
                .when()
                .post(NODE_ENDPOINT + UPDATE_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        final List<String> actualData = response.jsonPath().getList(TestConstants.DATA);
        final List<String> expectedData = Arrays.stream(new String[]{Message.UUID_MUST_NOT_BE_BLANK,
                Message.UUID_MUST_NOT_BE_BLANK})
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Collections.sort(actualData);
        Collections.sort(expectedData);

        assertEquals(expectedData, actualData);
    }

    /**
     * Tests if the database is initially empty by making a GET request to the user endpoint and verifying that the
     * response data is empty.
     */
    @Test
    @Order(3)
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
    @Order(4)
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
        assertEquals(TestConstants.TEST_NAME1, response.jsonPath().get(TestConstants.DATA_USERNAME));
    }

    /**
     * Tests if a graph entity can be posted via the JSON API without any nodes by making a POST request to the
     * node/graph endpoint and verifying the response.
     */
    @Test
    @Order(5)
    void anGraphEntityIsPostedViaJsonApiWithoutAnyNode() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-graph.json"), TestConstants.TEST_ID1, TestConstants.TEST_TITLE1))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        graphUuid1 = response.jsonPath().get(TestConstants.DATA_UUID);

        assertNotNull(response.jsonPath().get(TestConstants.DATA_UUID));
        assertEquals(TestConstants.TEST_TITLE1, response.jsonPath().get("data.title"));
    }

    /**
     * Tests if a node entity can be posted via the JSON API by making a POST request to the node endpoint and
     * verifying the response.
     */
    @Test
    @Order(6)
    void nodeEntityIsPostedViaJsonApi() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-node.json"), graphUuid1,
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
    @Order(7)
    void weCanGetThatNodeEntityNext() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON), graphUuid1))
                .when()
                .post(GRAPH_ENDPOINT + FILTER_ENDPOINT)
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
    @Order(8)
    void weCanUpdateThatNodeEntity() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_NODE_JSON), graphUuid1, nodeUuid1, TestConstants.TEST_TITLE4))
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
    @Order(9)
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
    @Order(10)
    void theNodesAreRelated() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("relate-node.json"), graphUuid1, nodeUuid1, nodeUuid2, nodeUuid1,
                        nodeUuid3, nodeUuid2, nodeUuid3))
                .when()
                .post(NODE_ENDPOINT)
                .then()
                .extract()
                .response();
    }

    /**
     * Tests if the relations of nodes can be retrieved via the graph UUID by making a POST request to the
     * graph endpoint and verifying the response.
     */
    @Test
    @Order(11)
    void weCanGetTheRelationOfNodesViaGraphUuid() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON), graphUuid1))
                .when()
                .post(GRAPH_ENDPOINT + FILTER_ENDPOINT)
                .then()
                .extract()
                .response();

        assertEquals("-", response.jsonPath().get("data.relations[0].name"));
    }

    /**
     * Tests if a graph can be created and nodes can be bound to it and the relationships can be expanded.
     */
    @Test
    @Order(12)
    public void weCanCreateGraphAndNodesAndBindRelationshipsToExpand() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-graph-nodes-to-expand.json"), TestConstants.TEST_ID1))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        graphUuid2 = response.jsonPath().get(TestConstants.DATA_UUID);
        nodeUuid2 = response.jsonPath().get(TestConstants.DATA_NODES_0_UUID);

        assertNotNull(response.jsonPath().get(TestConstants.DATA_UUID));
        assertNotNull(response.jsonPath().get(TestConstants.DATA_NODES_0_UUID));
    }

    /**
     * Tests if the nodes can be retrieved to the node/expand endpoint and verifying the response.
     * @param degree the degree of the node to expand.
     * @param count the expected number of nodes to be returned.
     */
    @ParameterizedTest
    @CsvSource({"1, 4", "2, 9", "3, 11", "4, 14", "5, 15", "6, 16", "7, 16", "0, 1", "-1, 16", "1000, 16"})
    @Order(13)
    public void weCanGetThatExpandNodesNextByGraphUuidAndNodesName(final String degree, final String count) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(NODE_ENDPOINT + "/expand?graphUuid=" + graphUuid2 + "&nodeUuid=" + nodeUuid2 + "&degree=" + degree)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        final List<String> actualNodeNames = response.jsonPath().getList("data.nodes.uuid");

        assertEquals(Integer.valueOf(count), actualNodeNames.size());
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
                .body(String.format(payload("delete-node.json"), graphUuid1, nodeUuid1))
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
