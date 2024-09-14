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

import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.spock.Testcontainers

import java.time.Duration

@Testcontainers
class DockerComposeITSpec extends AbstractITSpec {
//    def DockerComposeContainer COMPOSE = new DockerComposeContainer(new File("docker-compose.yml"))
//            .withExposedService(
//                    "web",
//                    WS_PORT,
//                    Wait.forHttp("/actuator/health").forStatusCode(200)
//            ).withStartupTimeout(Duration.ofMinutes(10))
}
