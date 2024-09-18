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

import io.restassured.http.ContentType
import javax.validation.constraints.NotNull
import org.junit.Assert
import io.restassured.RestAssured
import io.restassured.response.Response
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

abstract class AbstractITSpec extends Specification {

    protected static final int WS_PORT = 8080
    private static final int OK_CODE = 200
    private static final String USER_ENDPOINT = "/user"
    private static final String GRAPH_ENDPOINT = "/graph"
    private static final String NODE_ENDPOINT = "/node"
    private static final String NODE_GRAPH_ENDPOINT = "/node/graph"
    private static final String NODE_BIND_ENDPOINT = "/node/bind"
    private static final String CREATE_UPDATE_USER_JSON = "create-update-user.json"
    private static final String CREATE_GRAPH_JSON = "create-graph.json"
    private static final String UPDATE_GRAPH_JSON = "update-graph.json"
    private static final String CREATE_NODE_JSON = "create-node.json"
    private static final String REALTE_NODE_JSON = "relate-node.json"
    private static final String TEST_UIDCID = "6b47"
    private static final String TEST_NICK_NAME = "Jame"
    private static final String UPDATE_NICK_NAME = "Fame"
    private static final String TEST_GRAPH_TITLE = "Rus"
    private static final String UPDATE_GRAPH_TITLE = "Kas"
    private static final String TEST_NODE_TITLE_01 = "Mos"
    private static final String TEST_NODE_TITLE_02 = "Nos"
    private static final String TEST_NODE_TITLE_03 = "Aos"
    private static final String UPDATE_NODE_TITLE = "Los"

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

    def childSetupSpec() {
        // intentionally left blank
    }

    def childCleanupSpec() {
        // intentionally left blank
    }

    def setupSpec() {
        childSetupSpec()

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = WS_PORT
    }

    def cleanupSpec() {
        RestAssured.reset()

        childCleanupSpec()
    }

    def "JSON API allows for POSTing, GETing, PUTTing, and DELETing the user, graph and node"() {
        expect: "database is initially empty"
        Response getUserResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT)
        getUserResponse.then()
                .statusCode(OK_CODE)

        Assert.assertEquals([], getUserResponse.jsonPath().getList("data"))

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
                .statusCode(OK_CODE)

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
                .statusCode(OK_CODE)

        then: "we can GET that User entity with updated attribute"
        Response getUpdatedUserEntityResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
                .then()
                .extract()
                .response()

        getUpdatedUserEntityResponse.then()
                .statusCode(200)

        Assert.assertEquals(TEST_UIDCID, getUpdatedUserEntityResponse.jsonPath().get("data.uidcid"))
        Assert.assertEquals(UPDATE_NICK_NAME, getUpdatedUserEntityResponse.jsonPath().get("data.nickName"))

        when: "a Graph entity is POSTed via JSON API"
        Response postGraphResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_GRAPH_JSON), TEST_UIDCID, TEST_GRAPH_TITLE))
                .when()
                .post(NODE_GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        postGraphResponse.then()
                .statusCode(OK_CODE)

        then: "we can GET that Graph entity next"
        Response getGraphEntityResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
                .then()
                .extract()
                .response()

        getGraphEntityResponse.then()
                .statusCode(200)

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
                .statusCode(OK_CODE)

        then: "we can GET that Graph entity with updated attribute"
        Response getUpdatedGraphEntityResponse = RestAssured
                .given()
                .when()
                .get(GRAPH_ENDPOINT + "/" + getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"))
                .then()
                .extract()
                .response()

        getUpdatedGraphEntityResponse.then()
                .statusCode(200)

        Assert.assertEquals(getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"),
                getUpdatedGraphEntityResponse.jsonPath().get("data.uuid"))
        Assert.assertEquals(UPDATE_GRAPH_TITLE, getUpdatedGraphEntityResponse.jsonPath().get("data.title"))

        when: "the Nodes entity is POSTed via JSON API"
        Response postNodeResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_NODE_JSON),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"),
                        TEST_NODE_TITLE_01, TEST_NODE_TITLE_02, TEST_NODE_TITLE_03))
                .when()
                .post(NODE_ENDPOINT)
                .then()
                .extract()
                .response()

        postNodeResponse.then()
                .statusCode(OK_CODE)

        then: "we can GET that Node entity next"
        Response getNodeEntityResponse = RestAssured
                .given()
                .when()
                .get(GRAPH_ENDPOINT + "/" + getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"))
                .then()
                .extract()
                .response()

        getNodeEntityResponse.then()
                .statusCode(200)

        Assert.assertEquals(getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"),
                getNodeEntityResponse.jsonPath().get("data.uuid"))

        when: "we update that Node entity"
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_GRAPH_JSON),
                        getNodeEntityResponse.jsonPath().get("data.nodes[0].startNode.uuid"), UPDATE_NODE_TITLE))
                .when()
                .put(NODE_ENDPOINT)
                .then()
                .statusCode(OK_CODE)

        then: "we can GET that Node entity with updated attribute"
        Response getUpdatedNodeEntityResponse = RestAssured
                .given()
                .when()
                .get(NODE_ENDPOINT + "/" + getNodeEntityResponse.jsonPath().get("data.nodes[0].startNode.uuid"))
                .then()
                .extract()
                .response()

        getUpdatedNodeEntityResponse.then()
                .statusCode(200)

        Assert.assertEquals(getNodeEntityResponse.jsonPath().get("data.nodes[0].startNode.uuid"),
                getUpdatedNodeEntityResponse.jsonPath().get("data.uuid"))
        Assert.assertEquals(UPDATE_NODE_TITLE, getUpdatedNodeEntityResponse.jsonPath().get("data.title"))

        when: "the nodes are related"
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(REALTE_NODE_JSON),
                        getNodeEntityResponse.jsonPath().get("data.nodes[0].startNode.uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[1].startNode.uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[0].startNode.uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[2].startNode.uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[1].startNode.uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[2].startNode.uuid")))
                .when()
                .post(NODE_BIND_ENDPOINT)
                .then()
                .extract()
                .response()

        then: "we can get the relation of nodes via graph uuid"
        final Response getRelationResponse = RestAssured
                .given()
                .when()
                .get(GRAPH_ENDPOINT + "/" + getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"))
        getRelationResponse.then()
                .statusCode(OK_CODE)

        Assert.assertEquals("-", getRelationResponse.jsonPath().get("data.nodes[0].relation.name"))


        when: "the Node entity is deleted"
        final Response deleteNodeResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Arrays.asList(getNodeEntityResponse.jsonPath().get("data.nodes[1].startNode.uuid")))
                .when()
                .delete(NODE_ENDPOINT)
        deleteNodeResponse.then()
                .statusCode(OK_CODE)

        then: "that Node entity is not found in database anymore"
        Response getResponse4 = RestAssured
                .given()
                .when()
                .get(NODE_ENDPOINT + "/" + getNodeEntityResponse.jsonPath().get("data.nodes[1].startNode.uuid"))
        getResponse4.then()
                .statusCode(OK_CODE)

        Assert.assertEquals(null, getResponse4.jsonPath().getList("data"))

        when: "the Graph entity is deleted"
        final Response deleteGraphResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Arrays.asList(getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid")))
                .when()
                .delete(GRAPH_ENDPOINT)
        deleteGraphResponse.then()
                .statusCode(OK_CODE)

        then: "that Graph entity is not found in database anymore"
        Response getResponse3 = RestAssured
                .given()
                .when()
                .get(GRAPH_ENDPOINT + "/" + getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"))
        getResponse3.then()
                .statusCode(OK_CODE)

        Assert.assertEquals(null, getResponse3.jsonPath().getList("data"))

        when: "the User entity is deleted"
        final Response deleteUserResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Arrays.asList(TEST_UIDCID))
                .when()
                .delete(USER_ENDPOINT)
        deleteUserResponse.then()
                .statusCode(OK_CODE)

        then: "that User entity is not found in database anymore"
        Response getResponse2 = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
        getResponse2.then()
                .statusCode(OK_CODE)

        Assert.assertEquals(null, getResponse2.jsonPath().getList("data"))
    }
}
