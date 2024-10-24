/*
 * Copyright Paion Data
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
package com.paiondata.aristotle.controller

import com.paiondata.aristotle.AbstractITSpec
import com.paiondata.aristotle.base.TestConstants

import org.junit.jupiter.api.Assertions

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response

class NodeControllerITSpec extends AbstractITSpec {

    def "JSON API handles invalid graph and node creation requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-graph-nodes-to-valid.json"), uidcid, title, description,
                        temporaryId1, temporaryId2, fromId, relationName, toId))
                .when()
                .post(NODE_ENDPOINT + "/graph")
                .then()
                .extract()
                .response()

        def actualData = response.jsonPath().get(TestConstants.DATA) as List<String>
        def sortedActualData = actualData.sort()
        def sortedExpectedData = expectedData.sort()
        assert sortedActualData == sortedExpectedData

        where:
        uidcid   | title      | description   | temporaryId1 | temporaryId2 | fromId | relationName | toId  | expectedMsg                              | expectedData
        ""       | "title"    | "description" | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!"]
        "uidcid" | ""         | "description" | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["title must not be blank!"]
        "uidcid" | "title"    | ""            | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["description must not be blank!"]
        "uidcid" | "title"    | "description" | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["temporaryId must not null!"]
        "uidcid" | "title"    | "description" | "id1"        | ""           | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["temporaryId must not null!"]
        "uidcid" | "title"    | "description" | "id1"        | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["fromId must not be blank!"]
        "uidcid" | "title"    | "description" | "id1"        | "id2"        | "id1"  | ""           | "id2" | "Request parameter verification error: " | ["relation must not be blank!"]
        "uidcid" | "title"    | "description" | "id1"        | "id2"        | "id1"  | "relation"   | ""    | "Request parameter verification error: " | ["toId must not be blank!"]
        ""       | ""         | "description" | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "title must not be blank!"]
        ""       | "title"    | ""            | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "description must not be blank!"]
        ""       | "title"    | "description" | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "temporaryId must not null!"]
        ""       | "title"    | "description" | "id1"        | ""           | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "temporaryId must not null!"]
        ""       | "title"    | "description" | "id1"        | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "fromId must not be blank!"]
        ""       | "title"    | "description" | "id1"        | "id2"        | "id1"  | ""           | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "relation must not be blank!"]
        ""       | "title"    | "description" | "id1"        | "id2"        | "id1"  | "relation"   | ""    | "Request parameter verification error: " | ["uidcid must not be blank!", "toId must not be blank!"]
        "uidcid" | ""         | ""            | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["title must not be blank!", "description must not be blank!"]
        "uidcid" | ""         | "description" | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["title must not be blank!", "temporaryId must not null!"]
        "uidcid" | ""         | "description" | "id1"        | ""           | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["title must not be blank!", "temporaryId must not null!"]
        "uidcid" | ""         | "description" | "id1"        | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["title must not be blank!", "fromId must not be blank!"]
        "uidcid" | ""         | "description" | "id1"        | "id2"        | "id1"  | ""           | "id2" | "Request parameter verification error: " | ["title must not be blank!", "relation must not be blank!"]
        "uidcid" | ""         | "description" | "id1"        | "id2"        | "id1"  | "relation"   | ""    | "Request parameter verification error: " | ["title must not be blank!", "toId must not be blank!"]
        "uidcid" | "title"    | ""            | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["description must not be blank!", "temporaryId must not null!"]
        "uidcid" | "title"    | ""            | "id1"        | ""           | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["description must not be blank!", "temporaryId must not null!"]
        "uidcid" | "title"    | ""            | "id1"        | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["description must not be blank!", "fromId must not be blank!"]
        "uidcid" | "title"    | ""            | "id1"        | "id2"        | "id1"  | ""           | "id2" | "Request parameter verification error: " | ["description must not be blank!", "relation must not be blank!"]
        "uidcid" | "title"    | ""            | "id1"        | "id2"        | "id1"  | "relation"   | ""    | "Request parameter verification error: " | ["description must not be blank!", "toId must not be blank!"]
        "uidcid" | "title"    | "description" | ""           | ""           | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["temporaryId must not null!", "temporaryId must not null!"]
        "uidcid" | "title"    | "description" | ""           | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["temporaryId must not null!", "fromId must not be blank!"]
    }

    def "JSON API handles invalid node creation requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-nodes-to-valid.json"), graphUuid,
                        temporaryId1, temporaryId2, fromId, relationName, toId))
                .when()
                .post(NODE_ENDPOINT)
                .then()
                .extract()
                .response()

        def actualData = response.jsonPath().get(TestConstants.DATA) as List<String>
        def sortedActualData = actualData.sort()
        def sortedExpectedData = expectedData.sort()
        assert sortedActualData == sortedExpectedData

        where:
        graphUuid | temporaryId1 | temporaryId2 | fromId | relationName | toId  | expectedMsg                              | expectedData
        ""        | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uuid must not be blank!"]
        "id"      | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["temporaryId must not null!"]
        ""        | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uuid must not be blank!", "temporaryId must not null!"]
        "id"      | "id1"        | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["fromId must not be blank!"]
        "id"      | "id1"        | "id2"        | "id1"  | "relation"   | ""    | "Request parameter verification error: " | ["toId must not be blank!"]
        "id"      | "id1"        | "id2"        | ""     | "relation"   | ""    | "Request parameter verification error: " | ["fromId must not be blank!", "toId must not be blank!"]
    }

    def "JSON API handles invalid node binding requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("relate-node-to-valid.json"), fromId, relationName, toId))
                .when()
                .post(NODE_ENDPOINT + "/bind")
                .then()
                .extract()
                .response()

        def actualMsg = response.jsonPath().get("msg")
        assert actualMsg == expectedMsg

        where:
        fromId | relationName | toId  | expectedMsg
        ""     | "relation"   | "id2" | "bindNodes.dtos[0].fromId: uuid must not be blank!"
        "id1"  | ""           | "id2" | "bindNodes.dtos[0].relationName: relation must not be blank!"
        "id1"  | "relation"   | ""    | "bindNodes.dtos[0].toId: uuid must not be blank!"
    }

    def "JSON API handles invalid node updating requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_NODE_JSON), "", ""))
                .when()
                .post(NODE_ENDPOINT + "/update")
                .then()
                .extract()
                .response()

        Assertions.assertEquals("Request parameter verification error: ", response.jsonPath().get("msg"))
        Assertions.assertEquals("uuid must not be blank!", response.jsonPath().get("data[0]"))
    }
}
