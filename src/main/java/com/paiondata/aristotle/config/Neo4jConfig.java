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
package com.paiondata.aristotle.config;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Neo4j.
 */
@Configuration
public class Neo4jConfig {

    /**
     * Creates a new session for Neo4j.
     * @param driver the driver instance
     * @return the session instance
     */
    @Bean
    public Session neo4jSession(final Driver driver) {
        return driver.session();
    }
}
