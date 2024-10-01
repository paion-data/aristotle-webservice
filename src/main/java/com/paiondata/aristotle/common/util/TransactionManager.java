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

import org.neo4j.driver.Transaction;

/**
 * Transaction manager interface.
 */
public interface TransactionManager {

    /**
     * Begins a transaction.
     * @return a transaction object
     */
    Transaction beginTransaction();

    /**
     * Commits a transaction.
     * @param tx transaction object
     */
    void commitTransaction(Transaction tx);

    /**
     * Rolls back a transaction.
     * @param tx transaction object
     */
    void rollbackTransaction(Transaction tx);
}
