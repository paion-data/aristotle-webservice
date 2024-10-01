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
import org.neo4j.driver.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Transaction manager for Neo4j.
 */
@Component
public class Neo4jTransactionManager implements TransactionManager {

    private final Session neo4jSession;

    /**
     * Constructor.
     * @param neo4jSession Neo4j session.
     */
    @Autowired
    public Neo4jTransactionManager(final Session neo4jSession) {
        this.neo4jSession = neo4jSession;
    }

    /**
     * Begins a transaction.
     * @return Transaction object.
     */
    @Override
    public Transaction beginTransaction() {
        return neo4jSession.beginTransaction();
    }

    /**
     * Commits a transaction.
     * @param tx transaction object to commit.
     */
    @Override
    public void commitTransaction(final Transaction tx) {
        tx.commit();
    }

    /**
     * Rollbacks a transaction.
     * @param tx transaction object to rollback.
     */
    @Override
    public void rollbackTransaction(final Transaction tx) {
        tx.rollback();
    }
}
