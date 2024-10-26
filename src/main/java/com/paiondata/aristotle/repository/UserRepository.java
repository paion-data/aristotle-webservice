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
package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.User;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing users using Neo4j.
 *
 * This interface provides methods for CRUD operations on users and their relationships.
 */
@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {

    /**
     * Retrieves a user by their unique identifier (OIDC ID).
     *
     * @param oidcid the unique identifier of the user
     * @return the user
     */
    @Query("MATCH (u:User { oidcid: $oidcid }) RETURN u")
    User getUserByOidcid(String oidcid);

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    @Query("MATCH (u:User) RETURN u")
    List<User> findAll();

    /**
     * Checks if a user with the given OIDC ID exists.
     *
     * @param oidcid the unique identifier of the user
     * @return the count of users with the given OIDC ID
     */
    @Query("MATCH (u:User { oidcid: $oidcid }) RETURN count(u)")
    long checkOidcidExists(String oidcid);

    /**
     * Creates a new user.
     *
     * @param oidcid the unique identifier of the user
     * @param nickName the nickname of the user
     * @return the created user
     */
    @Query("CREATE (u:User { oidcid: $oidcid, nick_name: $nickName }) RETURN u")
    User createUser(@Param("oidcid") String oidcid,
                    @Param("nickName") String nickName);

    /**
     * Updates the nickname of a user.
     *
     * @param oidcid   the unique identifier of the user
     * @param nickName the new nickname of the user
     * @return the updated user
     */
    @Query("MATCH (u:User { oidcid: $oidcid }) SET u.nick_name = $nickName RETURN u")
    User updateUser(@Param("oidcid") String oidcid,
                    @Param("nickName") String nickName);

    /**
     * Deletes users by their OIDC IDs.
     *
     * @param oidcids the list of OIDC IDs of the users to be deleted
     */
    @Query("MATCH (u:User) WHERE u.oidcid IN $oidcids DETACH DELETE u")
    void deleteByOidcids(List<String> oidcids);

    /**
     * Retrieves the UUIDs of graphs associated with the given users.
     *
     * @param oidcids the list of OIDC IDs of the users
     * @return the list of UUIDs of the graphs
     */
    @Query("MATCH (u:User) WHERE u.oidcid IN $oidcids "
            + "WITH u "
            + "MATCH (u)-[r:RELATION]->(g:Graph) "
            + "RETURN g.uuid")
    List<String> getGraphUuidsByUserOidcid(List<String> oidcids);
}
