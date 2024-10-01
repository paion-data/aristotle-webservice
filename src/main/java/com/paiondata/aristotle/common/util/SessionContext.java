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
package com.paiondata.aristotle.common.util;

import org.neo4j.driver.Session;

/**
 * Session context for Neo4j.
 */
public class SessionContext {

    private static final ThreadLocal<Session> SESSION_HOLDER = new ThreadLocal<>();

    /**
     * Set session.
     * @param session Session to be set.
     */
    public static void setSession(final Session session) {
        SESSION_HOLDER.set(session);
    }

    /**
     * Get session.
     * @return Session.
     */
    public static Session getSession() {
        return SESSION_HOLDER.get();
    }

    /**
     * Remove session.
     */
    public static void removeSession() {
        SESSION_HOLDER.remove();
    }
}
