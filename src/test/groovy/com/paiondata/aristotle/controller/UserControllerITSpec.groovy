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
import org.springframework.http.HttpStatus

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response

class UserControllerITSpec extends AbstractITSpec {

    def "JSON API handles invalid user creation requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_UPDATE_USER_JSON), oidcid, nickName))
                .when()
                .post(USER_ENDPOINT)
                .then()
                .extract()
                .response()

        def actualData = response.jsonPath().get(TestConstants.DATA) as List<String>
        def sortedActualData = actualData.sort()
        def sortedExpectedData = expectedData.sort()
        assert sortedActualData == sortedExpectedData

        where:
        oidcid   | nickName       | expectedMsg                              | expectedData
        ""       | ""             | "Request parameter verification error: " | ["nickName must not be blank!", "oidcid must not be blank!"]
        ""       | "name"         | "Request parameter verification error: " | ["oidcid must not be blank!"]
        "id"     | ""             | "Request parameter verification error: " | ["nickName must not be blank!"]
    }

    def "JSON API handles invalid user updating requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_UPDATE_USER_JSON), oidcid, nickName))
                .when()
                .put(USER_ENDPOINT)
                .then()
                .extract()
                .response()

        def actualData = response.jsonPath().get(TestConstants.DATA) as List<String>
        def sortedActualData = actualData.sort()
        def sortedExpectedData = expectedData.sort()
        assert sortedActualData == sortedExpectedData

        where:
        oidcid   | nickName       | expectedMsg                              | expectedData
        ""       | ""             | "Request parameter verification error: " | ["nickName must not be blank!", "oidcid must not be blank!"]
        ""       | "name"         | "Request parameter verification error: " | ["oidcid must not be blank!"]
        "id"     | ""             | "Request parameter verification error: " | ["nickName must not be blank!"]
    }

    def "JSON API handles invalid user deleting requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("[]")
                .when()
                .delete(USER_ENDPOINT)
                .then()
                .extract()
                .response()

        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        Assertions.assertEquals("deleteUser.oidcids: oidcids must not be empty!", response.jsonPath().get("msg"))
    }
}
