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
 * GraphControllerIT is a test class that extends AbstractIT and contains integration tests for
 * the GraphController. These tests validate various operations such as retrieving, updating, deleting,
 * and creating graphs and nodes.
 */
@Transactional(propagation = Propagation.NEVER)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GraphControllerIT extends AbstractIT {

    /**
     * The embedded Neo4j database server used for testing.
     */
    private static Neo4j embeddedDatabaseServer;

    /**
     * A static string variable to store the UUID of a created graph.
     */
    private static String uuid;

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
     * Tests if the JSON API correctly handles invalid graph retrieving requests by returning a 400 Bad Request
     * status code and appropriate error messages.
     */
    @Test
    @Order(1)
    void jsonApiHandlesInvalidGraphRetrievingRequests() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON), ""))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        assertEquals(TestConstants.REQUEST_PARAM_ERROR, response.jsonPath().get(TestConstants.MSG));
        assertEquals(TestConstants.UUID_NOT_BLANK, response.jsonPath().get(TestConstants.DATA_0));
    }

    /**
     * Tests if the JSON API correctly handles invalid graph updating requests by returning a 400 Bad Request status
     * code and appropriate error messages.
     */
    @Test
    @Order(2)
    void jsonApiHandlesInvalidGraphUpdatingRequests() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_GRAPH_JSON), "", TestConstants.TEST_TITLE1))
                .when()
                .put(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        assertEquals(TestConstants.REQUEST_PARAM_ERROR, response.jsonPath().get(TestConstants.MSG));
        assertEquals(TestConstants.UUID_NOT_BLANK, response.jsonPath().get(TestConstants.DATA_0));
    }

    /**
     * Parameterized test to verify if the JSON API correctly handles invalid graph deleting requests by returning a
     * 400 Bad Request status code and appropriate error messages.
     *
     * @param oidcid The OIDC ID to be used in the request body.
     * @param expectedError1 The first expected error message.
     * @param expectedError2 The second expected error message.
     */
    @ParameterizedTest
    @CsvSource({
            "'' , oidcid must not be blank! , uuids must not be empty!",
            "id1 , uuids must not be empty! , ''"})
    @Order(3)
    void jsonApiHandlesInvalidGraphDeletingRequests(final String oidcid, final String expectedError1,
                                                    final String expectedError2) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("delete-graph-to-valid.json"), oidcid))
                .when()
                .delete(GRAPH_ENDPOINT)
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
     * Tests if the database is initially empty by making a GET request to the user endpoint and verifying that the
     * response data is empty.
     */
    @Test
    @Order(4)
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
    @Order(5)
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

        assertEquals(response.jsonPath().get("data.oidcid"), TestConstants.TEST_ID1);
        assertEquals(response.jsonPath().get("data.nickName"), TestConstants.TEST_NAME1);
    }

    /**
     * Tests if a graph and its nodes can be created and relationships bound in one step by making a POST request
     * to the node/graph endpoint and verifying the response.
     */
    @Test
    @Order(6)
    void weCanCreateGraphAndNodesAndBindRelationshipsInOneStep() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-graph-nodes.json"), TestConstants.TEST_ID1,
                        TestConstants.TEST_TITLE1, TestConstants.BLUE, TestConstants.BLUE, TestConstants.GREEN,
                        TestConstants.BLUE, TestConstants.BLUE, TestConstants.BLUE, TestConstants.BLUE))
                .when()
                .post("/node/graph")
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        uuid = response.jsonPath().get(TestConstants.DATA_UUID);

        assertNotNull(response.jsonPath().get(TestConstants.DATA_UUID));
        assertEquals(response.jsonPath().get(TestConstants.DATA_TITLE), TestConstants.TEST_TITLE1);
    }

    /**
     * Tests if a graph entity can be retrieved using filter and page parameters by making a POST request to the
     * graph endpoint and verifying the response.
     */
    @Test
    @Order(7)
    void weCanGetThatGraphEntityNextByFilterAndPageParams() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_FILTER_JSON),
                        uuid, TestConstants.BLUE,
                        TestConstants.DEFALUT_PAGE_SIZE, TestConstants.DEFALUT_PAGE_NUMBER))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(TestConstants.DEFALUT_PAGE_NUMBER, response.jsonPath().get(TestConstants.DATA_PAGENUMBER));
        assertEquals(TestConstants.DEFALUT_PAGE_SIZE, response.jsonPath().get(TestConstants.DATA_PAGESIZE));
        assertEquals(TestConstants.EXPECT_TOTAL_COUNT_02, response.jsonPath().get(TestConstants.DATA_TOTALCOUNT));
        assertEquals(TestConstants.TEST_TITLE1, response.jsonPath().get(TestConstants.DATA_TITLE));
        assertEquals(TestConstants.BLUE, response.jsonPath().get("data.nodes[0].properties.color"));
        assertEquals(TestConstants.BLUE, response.jsonPath().get("data.nodes[1].properties.color"));
        assertNotNull(response.jsonPath().get("data.relations[0]"));
        assertNull(response.jsonPath().get("data.relations[1]"));

        response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_FILTER_JSON),
                        uuid, TestConstants.BLUE,
                        TestConstants.TEST_PAGE_SIZE_01, TestConstants.DEFALUT_PAGE_NUMBER))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(TestConstants.DEFALUT_PAGE_NUMBER, response.jsonPath().get(TestConstants.DATA_PAGENUMBER));
        assertEquals(TestConstants.TEST_PAGE_SIZE_01, response.jsonPath().get(TestConstants.DATA_PAGESIZE));
        assertEquals(TestConstants.EXPECT_TOTAL_COUNT_03, response.jsonPath().get(TestConstants.DATA_TOTALCOUNT));

        response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_FILTER_JSON),
                        uuid, TestConstants.BLUE,
                        TestConstants.TEST_PAGE_SIZE_01, TestConstants.TEST_PAGE_NUMBER_01))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(TestConstants.TEST_PAGE_NUMBER_01, response.jsonPath().get(TestConstants.DATA_PAGENUMBER));
        assertEquals(TestConstants.TEST_PAGE_SIZE_01, response.jsonPath().get(TestConstants.DATA_PAGESIZE));
        assertEquals(TestConstants.EXPECT_TOTAL_COUNT_03, response.jsonPath().get(TestConstants.DATA_TOTALCOUNT));
    }

    /**
     * Tests if a graph entity can be updated by making a PUT request to the graph endpoint and verifying the response.
     */
    @Test
    @Order(8)
    void weCanUpdateThatGraphEntity() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_GRAPH_JSON),
                        uuid, TestConstants.TEST_TITLE2))
                .when()
                .put(GRAPH_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * Tests if a graph entity with updated attributes can be retrieved by making a GET request to the user endpoint
     * and verifying the response.
     */
    @Test
    @Order(9)
    void weCanGetThatGraphEntityWithUpdatedAttribute() {
        final Response response = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TestConstants.TEST_ID1)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(uuid, response.jsonPath().get("data.graphs[0].uuid"));
        assertEquals(TestConstants.TEST_TITLE2, response.jsonPath().get("data.graphs[0].title"));
    }

    /**
     * Tests if a graph can be deleted by making a DELETE request to the graph endpoint and verifying the response.
     */
    @Test
    @Order(10)
    void weCanDeleteGraph() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("delete-graph.json"), TestConstants.TEST_ID1, uuid))
                .when()
                .delete(GRAPH_ENDPOINT);
        response.then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * Tests if a deleted graph entity is no longer found in the database by making a POST request to the graph endpoint
     * and verifying the response.
     */
    @Test
    @Order(11)
    void thatGraphEntityIsNotFoundInDatabaseAnyMore() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON), uuid))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        assertEquals("The graph with UUID '" + uuid + "' does not exist", response.jsonPath().get(TestConstants.MSG));
    }
}
