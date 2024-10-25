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

/**
 * CAUTION: This spec runs for every sub-class. Please do NOT add tests in this file except for common test setup, such
 * as Docker Compose startup logic
 */
@Testcontainers
abstract class AbstractITSpec extends Specification {

    static final int WS_PORT = 8080
    static final String USER_ENDPOINT = "/user"
    static final String GRAPH_ENDPOINT = "/graph"
    static final String NODE_ENDPOINT = "/node"
    static final String CREATE_UPDATE_USER_JSON = "create-update-user.json"
    static final String GET_GRAPH_JSON = "get-graph.json"
    static final String UPDATE_GRAPH_JSON = "update-graph.json"

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
}
