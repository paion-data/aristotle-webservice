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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
 * Integration tests for the UserController.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIT extends AbstractIT {

    /**
     * Tests that the user controller handles invalid user creation requests.
     *
     * @param oidcid      the oidcid of the user
     * @param nickName    the nickname of the user
     * @param expectedError1   the expected error message for the oidcid
     * @param expectedError2   the expected error message for the nickname
     */
    @ParameterizedTest
    @CsvSource({
            "'', '', nickName must not be blank!, oidcid must not be blank!",
            "'', name, oidcid must not be blank!, ''",
            "id, '', nickName must not be blank!, ''"})
    @Order(1)
    void jsonApiHandlesInvalidUserCreationRequests(final String oidcid, final String nickName,
                                                          final String expectedError1, final String expectedError2) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_UPDATE_USER_JSON), oidcid, nickName))
                .when()
                .post(USER_ENDPOINT)
                .then()
                .extract()
                .response();

        final List<String> actualData = response.jsonPath().getList(TestConstants.DATA);
        final List<String> expectedData = Arrays.stream(new String[]{expectedError1, expectedError2})
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Collections.sort(actualData);
        Collections.sort(expectedData);

        assertEquals(expectedData, actualData);
    }

    /**
     * Tests that the user controller handles invalid user deleting requests.
     */
    @Test
    @Order(2)
    void jsonApiHandlesInvalidUserDeletingRequests() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("[]")
                .when()
                .delete(USER_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        assertEquals("deleteUser.oidcids: oidcids must not be empty!", response.jsonPath().get(TestConstants.MSG));
    }

    /**
     * Tests that the user controller returns an empty list when there are no user entities in the database.
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

        final List<?> data = response.jsonPath().getList(TestConstants.DATA);
        assertTrue(data.isEmpty());
    }

    /**
     * Tests that the user controller can post a user entity via json api.
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
        assertEquals(TestConstants.TEST_NAME1, response.jsonPath().get(TestConstants.DATA_NICKNAME));
    }

    /**
     * Tests that the user controller can get a user entity via json api.
     */
    @Test
    @Order(5)
    void weCanGetThatUserEntityNext() {
        final Response response = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + TestConstants.SLASH + TestConstants.TEST_ID1)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(200);

        assertEquals(TestConstants.TEST_ID1, response.jsonPath().get(TestConstants.DATA_OIDCID));
        assertEquals(TestConstants.TEST_NAME1, response.jsonPath().get(TestConstants.DATA_NICKNAME));
    }

    /**
     * Tests that the user controller can update a user entity via json api.
     */
    @Test
    @Order(6)
    void anotherUserEntityIsPostedViaJsonApi() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_UPDATE_USER_JSON), TestConstants.TEST_ID2, TestConstants.TEST_NAME2))
                .when()
                .post(USER_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(TestConstants.TEST_ID2, response.jsonPath().get(TestConstants.DATA_OIDCID));
        assertEquals(TestConstants.TEST_NAME2, response.jsonPath().get(TestConstants.DATA_NICKNAME));
    }

    /**
     * Tests that the user controller can get all user entities via json api.
     */
    @Test
    @Order(7)
    void weCanGetAllUserEntityNext() {
        final Response response = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        final List<String> expectedData = Arrays.asList(TestConstants.TEST_ID1, TestConstants.TEST_ID2);

        final List<String> actualData = response.jsonPath().getList(TestConstants.DATA_OIDCID);

        assertTrue(actualData.containsAll(expectedData));
    }

    /**
     * Tests that the user controller can update a user entity via json api.
     */
    @Test
    @Order(8)
    void weCanUpdateThatUserEntity() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_UPDATE_USER_JSON), TestConstants.TEST_ID1, TestConstants.TEST_NAME2))
                .when()
                .put(USER_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * Tests that the user controller can get a user entity via json api.
     */
    @Test
    @Order(9)
    void weCanGetThatUserEntityWithUpdatedAttribute() {
        final Response response = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + TestConstants.SLASH + TestConstants.TEST_ID1)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(TestConstants.TEST_ID1, response.jsonPath().get(TestConstants.DATA_OIDCID));
        assertEquals(TestConstants.TEST_NAME2, response.jsonPath().get(TestConstants.DATA_NICKNAME));
    }

    /**
     * Tests that the user controller can delete a user entity via json api.
     */
    @Test
    @Order(10)
    void thatUserEntityIsDeleted() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Arrays.asList(TestConstants.TEST_ID1, TestConstants.TEST_ID2))
                .when()
                .delete(USER_ENDPOINT);
        response.then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * Tests that the user controller can get a user entity via json api.
     */
    @Test
    @Order(11)
    void thatUserEntityIsNotFoundInDatabaseAnymore() {
        final Response response = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT)
                .then()
                .extract()
                .response();

        response.then()
                .statusCode(HttpStatus.OK.value());

        final List<?> data = response.jsonPath().getList(TestConstants.DATA);
        assertTrue(data.isEmpty());
    }
}
