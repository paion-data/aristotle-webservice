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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.validation.constraints.NotNull;
import io.restassured.RestAssured;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * An abstract base class for integration tests that provides common setup and utility methods.
 * This class is configured to run with a random port for the web environment, ensuring that
 * tests do not conflict with other running services. It also ensures that transactions are
 * never propagated, preventing any accidental data persistence during tests.
 */
@Transactional(propagation = Propagation.NEVER)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractIT {

    /**
     * The endpoint for user-related operations.
     */
    static final String USER_ENDPOINT = "/user";

    /**
     * The endpoint for graph-related operations.
     */
    static final String GRAPH_ENDPOINT = "/graph";

    /**
     * The endpoint for filter-related operations.
     */
    static final String FILTER_ENDPOINT = "/filter";

    /**
     * The endpoint for update-related operations.
     */
    static final String UPDATE_ENDPOINT = "/update";

    /**
     * The endpoint for node-related operations.
     */
    static final String NODE_ENDPOINT = "/node";

    /**
     * The JSON file used for creating or updating users.
     */
    static final String CREATE_UPDATE_USER_JSON = "create-update-user.json";

    /**
     * The JSON file used for retrieving graphs.
     */
    static final String GET_GRAPH_JSON = "get-graph.json";

    /**
     * The JSON file used for updating graphs.
     */
    static final String UPDATE_GRAPH_JSON = "update-graph.json";

    /**
     * The JSON file used for updating nodes.
     */
    static final String UPDATE_NODE_JSON = "update-node.json";

    /**
     * The JSON file used for retrieving filtered graphs.
     */
    static final String GET_GRAPH_FILTER_JSON = "get-graph-filter.json";

    /**
     * The embedded Neo4j database server used for testing.
     */
    private static Neo4j embeddedDatabaseServer;

    /**
     * The port number on which the application is running.
     */
    @LocalServerPort
    protected int port;

    /**
     * Sets up the embedded Neo4j database server before all tests.
     */
    @BeforeAll
    static void setUp() {
        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .withConfig(BoltConnector.enabled, true)
                .withConfig(BoltConnector.listen_address, new SocketAddress("localhost", 7687))
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
     * Configures the base URI and port for REST requests before each test.
     */
    @BeforeEach
    public void set() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    /**
     * Resets the REST Assured configuration after each test.
     */
    @AfterEach
    void reset() {
        RestAssured.reset();
    }

    /**
     * Loads a resource file, under "payload" resource directory, as a {@code String} object given that resource file
     * name.
     * <p>
     * All new line characters ("\n") will be removed as well.
     *
     * @param resourceName  The specified resource file name
     *
     * @return the resource file content as a single {@code String}
     *
     * @throws NullPointerException if {@code resourceName} is {@code null}
     * @throws IllegalStateException if an I/O error occurs reading from the resource file stream
     * @throws IllegalArgumentException  if resource path is not formatted strictly according to RFC2396 and cannot be
     * converted to a URI.
     */
    @NotNull
    protected String payload(final @NotNull String resourceName) {
        return resource("payload", resourceName);
    }

    /**
     * Loads a resource file content as a {@code String} object according to a provided resource path.
     * <p>
     * The resource path is defined by two components:
     * <ol>
     *     <li> a relative path under "resource" folder
     *     <li> the name of the resource file
     * </ol>
     * For example, when we would like to read
     * "src/test/resources/payload/metadata/multiple-fields-metadata-request.json", then the relative path is
     * "payload/metadata" and the name of the resource file is "multiple-fields-metadata-request.json"
     *
     * @param resourceDirPath  The relative path under "resource" folder
     * @param resourceFilename  The specified resource file name
     *
     * @return the resource file content as a single {@code String}
     *
     * @throws NullPointerException if {@code resourceFilename} is {@code null}
     * @throws IllegalStateException if an I/O error occurs reading from the resource file stream
     * @throws IllegalArgumentException  if resource path is not formatted strictly according to RFC2396 and cannot be
     * converted to a URI.
     */
    @NotNull
    protected String resource(final @NotNull String resourceDirPath, final @NotNull String resourceFilename) {
        Objects.requireNonNull(resourceDirPath);
        Objects.requireNonNull(resourceFilename);

        final String resource = String.format(
                "%s/%s",
                resourceDirPath.endsWith("/")
                        ? resourceDirPath.substring(0, resourceDirPath.length() - 1)
                        : resourceDirPath,
                resourceFilename
        );

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
            );
        } catch (final IOException exception) {
            final String message = String.format("Error reading file stream from '%s'", resource);
            throw new IllegalStateException(message, exception);
        } catch (final URISyntaxException exception) {
            final String message = String.format("'%s' is not a valid URI fragment", resource);
            throw new IllegalArgumentException(message, exception);
        }
    }
}
