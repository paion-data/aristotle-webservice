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
 * Transaction context for Neo4j.
 */
public class TransactionContext {

    private static final ThreadLocal<Transaction> TRANSACTION_HOLDER = new ThreadLocal<>();

    /**
     * Set transaction.
     * @param transaction Session to be set.
     */
    public static void setTransaction(final Transaction transaction) {
        TRANSACTION_HOLDER.set(transaction);
    }

    /**
     * Get transaction.
     * @return Transaction.
     */
    public static Transaction getTransaction() {
        return TRANSACTION_HOLDER.get();
    }

    /**
     * Remove transaction.
     */
    public static void removeTransaction() {
        TRANSACTION_HOLDER.remove();
    }
}
