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
package com.paiondata.aristotle.aop;

import com.paiondata.aristotle.common.util.TransactionContext;
import com.paiondata.aristotle.common.util.TransactionManager;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Aspect for handling Neo4j transactions.
 */
@Aspect
@Component
@Order(1)
public class Neo4jTransactionAspect {

    @Resource
    private TransactionManager neo4jTransactionManager;

    @Autowired
    private Session neo4jSession;

    /**
     * Transaction manager.
     * @param joinPoint Join point
     * @return the result of the method
     * @throws Throwable if any error occurs
     */
    @Around("@annotation(com.paiondata.aristotle.common.annotion.Neo4jTransactional)")
    public Object manageTransaction(final ProceedingJoinPoint joinPoint) throws Throwable {
        Transaction tx = null;
        try {
            tx = neo4jSession.beginTransaction();
            // Stores the Transaction to ThreadLocal
            TransactionContext.setTransaction(tx);

            final Object result = joinPoint.proceed(joinPoint.getArgs());

            neo4jTransactionManager.commitTransaction(tx);
            return result;
        } catch (final Exception e) {
            if (tx != null) {
                neo4jTransactionManager.rollbackTransaction(tx);
            }
            throw e;
        } finally {
            // Clears a Session in ThreadLocal
            TransactionContext.removeTransaction();
            if (tx != null && tx.isOpen()) {
                tx.close();
            }
        }
    }
}
