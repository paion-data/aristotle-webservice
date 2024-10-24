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
package com.paiondata.aristotle

import com.paiondata.aristotle.base.TestConstants

import org.springframework.http.HttpStatus
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.spock.Testcontainers

import io.restassured.http.ContentType
import org.junit.jupiter.api.Assertions

import spock.lang.Shared

import java.time.Duration

import javax.validation.constraints.NotNull
import org.junit.Assert
import io.restassured.RestAssured
import io.restassured.response.Response
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

@Testcontainers
abstract class AbstractITSpec extends Specification {

    static final int WS_PORT = 8080
    static final String USER_ENDPOINT = "/user"
    static final String GRAPH_ENDPOINT = "/graph"
    static final String NODE_ENDPOINT = "/node"
    static final String CREATE_UPDATE_USER_JSON = "create-update-user.json"
    static final String UPDATE_GRAPH_JSON = "update-graph.json"
    static final String GET_GRAPH_JSON = "get-graph.json"
    static final String DELETE_GRAPH_JSON = "delete-graph.json"
    static final String UPDATE_NODE_JSON = "update-node.json"
    static final String TEST_UIDCID = "6b47"
    static final String TEST_NICK_NAME = "Jame"
    static final String UPDATE_NICK_NAME = "Fame"
    static final String TEST_GRAPH_TITLE = "Rus"
    static final String UPDATE_GRAPH_TITLE = "Kas"
    static final String UPDATE_NODE_TITLE = "Los"

    @Shared
    DockerComposeContainer COMPOSE = new DockerComposeContainer(new File("docker-compose.yml"))
            .withExposedService(
                    "web",
                    WS_PORT,
                    Wait.forHttp("/actuator/health").forStatusCode(HttpStatus.OK.value())
            ).withStartupTimeout(Duration.ofMinutes(10))

    def childSetupSpec() {
        // intentionally left blank
    }

    def childCleanupSpec() {
        // intentionally left blank
    }

    def setupSpec() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = WS_PORT

        childSetupSpec()
    }

    def cleanupSpec() {
        RestAssured.reset()

        childCleanupSpec()
    }

    @NotNull
    protected String payload(final @NotNull String resourceName) {
        return resource("payload", resourceName)
    }

    @NotNull
    protected String resource(final @NotNull String resourceDirPath, final @NotNull String resourceFilename) {
        Objects.requireNonNull(resourceDirPath)
        Objects.requireNonNull(resourceFilename)

        final String resource = String.format(
                "%s/%s",
                resourceDirPath.endsWith("/")
                        ? resourceDirPath.substring(0, resourceDirPath.length() - 1)
                        : resourceDirPath,
                resourceFilename
        )

        try {
            return new String(
                    Files.readAllBytes(
                            Paths.get(
                                    Objects.requireNonNull(
                                            this.getClass()
                                                    .getClassLoader()
                                                    .getResource(resource)
                                    )
                                            .toURI()
                            )
                    )
            )
        } catch (final IOException exception) {
            final String message = String.format("Error reading file stream from '%s'", resource)
            throw new IllegalStateException(message, exception)
        } catch (final URISyntaxException exception) {
            final String message = String.format("'%s' is not a valid URI fragment", resource)
            throw new IllegalArgumentException(message, exception)
        }
    }

    def "JSON API allows for POSTing, GETing, PUTTing, and DELETing the user, graph and node"() {
        expect: "database is initially empty"
        Response getUserResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT)
        getUserResponse.then()
                .statusCode(HttpStatus.OK.value())

        Assert.assertEquals([], getUserResponse.jsonPath().getList(TestConstants.DATA))

        when: "an User entity is POSTed via JSON API"
        Response postUserResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                    .body(String.format(payload(CREATE_UPDATE_USER_JSON), TEST_UIDCID, TEST_NICK_NAME))
                .when()
                .post(USER_ENDPOINT)
                .then()
                .extract()
                .response()

        postUserResponse.then()
                .statusCode(HttpStatus.OK.value())

        Assertions.assertEquals(postUserResponse.jsonPath().get("data.uidcid"), TEST_UIDCID)
        Assertions.assertEquals(postUserResponse.jsonPath().get("data.nickName"), TEST_NICK_NAME)

        then: "we can GET that User entity next"
        Response getUserEntityResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
                .then()
                .extract()
                .response()

        getUserEntityResponse.then()
                .statusCode(200)

        Assert.assertEquals(TEST_UIDCID, getUserEntityResponse.jsonPath().get("data.uidcid"))
        Assert.assertEquals(TEST_NICK_NAME, getUserEntityResponse.jsonPath().get("data.nickName"))

        when: "we update that User entity"
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_UPDATE_USER_JSON), TEST_UIDCID, UPDATE_NICK_NAME))
                .when()
                .put(USER_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())

        then: "we can GET that User entity with updated attribute"
        Response getUpdatedUserEntityResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
                .then()
                .extract()
                .response()

        getUpdatedUserEntityResponse.then()
                .statusCode(HttpStatus.OK.value())

        Assert.assertEquals(TEST_UIDCID, getUpdatedUserEntityResponse.jsonPath().get("data.uidcid"))
        Assert.assertEquals(UPDATE_NICK_NAME, getUpdatedUserEntityResponse.jsonPath().get("data.nickName"))

        when: "a Graph entity is POSTed via JSON API"
        Response postGraphResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-graph.json"), TEST_UIDCID, TEST_GRAPH_TITLE))
                .when()
                .post("/node/graph")
                .then()
                .extract()
                .response()

        postGraphResponse.then()
                .statusCode(HttpStatus.OK.value())

        Assertions.assertNotNull(postGraphResponse.jsonPath().get("data.uuid"))
        Assertions.assertEquals(postGraphResponse.jsonPath().get("data.title"), TEST_GRAPH_TITLE)

        then: "we can GET that Graph entity next"
        Response getGraphEntityResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
                .then()
                .extract()
                .response()

        getGraphEntityResponse.then()
                .statusCode(HttpStatus.OK.value())

        Assert.assertEquals(TEST_UIDCID, getGraphEntityResponse.jsonPath().get("data.uidcid"))
        Assert.assertEquals(TEST_GRAPH_TITLE, getGraphEntityResponse.jsonPath().get("data.graphs[0].title"))

        when: "we update that Graph entity"
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_GRAPH_JSON),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"), UPDATE_GRAPH_TITLE))
                .when()
                .put(GRAPH_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())

        then: "we can GET that Graph entity with updated attribute"
        Response getUpdatedGraphEntityResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
                .then()
                .extract()
                .response()

        getUpdatedGraphEntityResponse.then()
                .statusCode(HttpStatus.OK.value())

        Assert.assertEquals(getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"),
                getUpdatedGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"))
        Assert.assertEquals(UPDATE_GRAPH_TITLE, getUpdatedGraphEntityResponse.jsonPath().get("data.graphs[0].title"))

        when: "the Nodes entity is POSTed via JSON API"
        Response postNodeResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-node.json"),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"),
                        "Mos", "Nos", "Aos"))
                .when()
                .post(NODE_ENDPOINT)
                .then()
                .extract()
                .response()

        postNodeResponse.then()
                .statusCode(HttpStatus.OK.value())

        then: "we can GET that Node entity next"
        Response getNodeEntityResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid")))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        getNodeEntityResponse.then()
                .statusCode(200)

        Assert.assertNotNull(getNodeEntityResponse.jsonPath().get("data.nodes[0].properties.title"))
        Assert.assertNotNull(getNodeEntityResponse.jsonPath().get("data.nodes[1].properties.title"))
        Assert.assertNotNull(getNodeEntityResponse.jsonPath().get("data.nodes[2].properties.title"))

        when: "we update that Node entity"
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_NODE_JSON),
                        getNodeEntityResponse.jsonPath().get("data.nodes[0].uuid"), UPDATE_NODE_TITLE))
                .when()
                .post(NODE_ENDPOINT + "/update")
                .then()
                .statusCode(HttpStatus.OK.value())

        then: "we can GET that Node entity with updated attribute"
        Response getUpdatedNodeEntityResponse = RestAssured
                .given()
                .when()
                .get(NODE_ENDPOINT + "/" + getNodeEntityResponse.jsonPath().get("data.nodes[0].uuid"))
                .then()
                .extract()
                .response()

        getUpdatedNodeEntityResponse.then()
                .statusCode(HttpStatus.OK.value())

        Assert.assertEquals(getNodeEntityResponse.jsonPath().get("data.nodes[0].uuid"),
                getUpdatedNodeEntityResponse.jsonPath().get("data.uuid"))
        Assert.assertEquals(UPDATE_NODE_TITLE, getUpdatedNodeEntityResponse.jsonPath().get("data.properties.title"))

        when: "the nodes are related"
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("relate-node.json"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[0].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[1].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[0].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[2].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[1].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[2].uuid")))
                .when()
                .post("/node/bind")
                .then()
                .extract()
                .response()

        then: "we can get the relation of nodes via graph uuid"
        final Response getRelationResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid")))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        Assert.assertEquals("-", getRelationResponse.jsonPath().get("data.relations[0].name"))

        when: "the Node entity is deleted"
        final Response deleteNodeResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("delete-node.json"),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[1].startNode.uuid")))
                .when()
                .delete(NODE_ENDPOINT)
        deleteNodeResponse.then()
                .statusCode(HttpStatus.OK.value())

        then: "that Node entity is not found in database anymore"
        Response getResponse4 = RestAssured
                .given()
                .when()
                .get(NODE_ENDPOINT + "/" + getNodeEntityResponse.jsonPath().get("data.nodes[1].startNode.uuid"))
        getResponse4.then()
                .statusCode(HttpStatus.OK.value())

        Assert.assertEquals(null, getResponse4.jsonPath().get(TestConstants.DATA))

        when: "create graph and nodes and bind relationships in one step"
        Response GraphResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-graph-nodes.json"), TEST_UIDCID, TEST_GRAPH_TITLE,
                        TestConstants.BLUE, TestConstants.BLUE, TestConstants.GREEN,
                        TestConstants.BLUE, TestConstants.BLUE, TestConstants.BLUE, TestConstants.BLUE))
                .when()
                .post("/node/graph")
                .then()
                .extract()
                .response()

        postGraphResponse.then()
                .statusCode(HttpStatus.OK.value())

        Assertions.assertNotNull(postGraphResponse.jsonPath().get("data.uuid"))
        Assertions.assertEquals(postGraphResponse.jsonPath().get("data.title"), TEST_GRAPH_TITLE)

        then: "we can GET that Graph entity next by filter and page params"
        Response getGraphEntityFilterResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("get-graph-filter.json"),
                        GraphResponse.jsonPath().get("data.uuid"), TestConstants.BLUE,
                        TestConstants.DEFALUT_PAGE_SIZE, TestConstants.DEFALUT_PAGE_NUMBER))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        getGraphEntityFilterResponse.then()
                .statusCode(HttpStatus.OK.value())

        Assert.assertEquals(TestConstants.DEFALUT_PAGE_NUMBER, getGraphEntityFilterResponse
                .jsonPath().get("data.pageNumber"))
        Assert.assertEquals(TestConstants.DEFALUT_PAGE_SIZE, getGraphEntityFilterResponse
                .jsonPath().get("data.pageSize"))
        Assert.assertEquals(TestConstants.EXPECT_TOTAL_COUNT_02, getGraphEntityFilterResponse
                .jsonPath().get("data.totalCount"))
        Assert.assertEquals(TEST_GRAPH_TITLE,
                getGraphEntityFilterResponse.jsonPath().get("data.title"))
        Assert.assertEquals(TestConstants.BLUE,
                getGraphEntityFilterResponse.jsonPath().get("data.nodes[0].properties.color"))
        Assert.assertEquals(TestConstants.BLUE,
                getGraphEntityFilterResponse.jsonPath().get("data.nodes[1].properties.color"))
        Assert.assertNotNull(getGraphEntityFilterResponse.jsonPath().get("data.relations[0]"))
        Assert.assertNull(getGraphEntityFilterResponse.jsonPath().get("data.relations[1]"))

        Response getGraphEntityFilterResponsePage1 = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("get-graph-filter.json"),
                        GraphResponse.jsonPath().get("data.uuid"), TestConstants.BLUE,
                        TestConstants.TEST_PAGE_SIZE_01, TestConstants.DEFALUT_PAGE_NUMBER))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        getGraphEntityFilterResponsePage1.then()
                .statusCode(HttpStatus.OK.value())

        Assert.assertEquals(TestConstants.DEFALUT_PAGE_NUMBER, getGraphEntityFilterResponsePage1
                .jsonPath().get("data.pageNumber"))
        Assert.assertEquals(TestConstants.TEST_PAGE_SIZE_01, getGraphEntityFilterResponsePage1
                .jsonPath().get("data.pageSize"))
        Assert.assertEquals(TestConstants.EXPECT_TOTAL_COUNT_03, getGraphEntityFilterResponsePage1
                .jsonPath().get("data.totalCount"))

        Response getGraphEntityFilterResponsePage2 = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("get-graph-filter.json"),
                        GraphResponse.jsonPath().get("data.uuid"), TestConstants.BLUE,
                        TestConstants.TEST_PAGE_SIZE_01, TestConstants.TEST_PAGE_NUMBER_01))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        getGraphEntityFilterResponsePage2.then()
                .statusCode(200)

        Assert.assertEquals(TestConstants.TEST_PAGE_NUMBER_01, getGraphEntityFilterResponsePage2
                .jsonPath().get("data.pageNumber"))
        Assert.assertEquals(TestConstants.TEST_PAGE_SIZE_01, getGraphEntityFilterResponsePage2
                .jsonPath().get("data.pageSize"))
        Assert.assertEquals(TestConstants.EXPECT_TOTAL_COUNT_03, getGraphEntityFilterResponsePage2
                .jsonPath().get("data.totalCount"))

        when: "the Graph entity is deleted"
        final Response deleteGraphResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload((DELETE_GRAPH_JSON)), TEST_UIDCID,
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid")))
                .when()
                .delete(GRAPH_ENDPOINT)
        deleteGraphResponse.then()
                .statusCode(HttpStatus.OK.value())

        then: "that Graph entity is not found in database anymore"
        Response getResponse3 = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid")))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        Assert.assertEquals(null, getResponse3.jsonPath().getList(TestConstants.DATA))

        when: "the User entity is deleted"
        final Response deleteUserResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Arrays.asList(TEST_UIDCID))
                .when()
                .delete(USER_ENDPOINT)
        deleteUserResponse.then()
                .statusCode(HttpStatus.OK.value())

        then: "that User entity is not found in database anymore"
        Response getResponse2 = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
        getResponse2.then()
                .statusCode(HttpStatus.OK.value())

        Assert.assertEquals(null, getResponse2.jsonPath().getList(TestConstants.DATA))
    }
}
