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
     * Retrieves a user by their unique identifier (UID/CID).
     *
     * @param uidcid the unique identifier of the user
     * @return the user
     */
    @Query("MATCH (u:User { uidcid: $uidcid }) RETURN u")
    User getUserByUidcid(String uidcid);

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    @Query("MATCH (u:User) RETURN u")
    List<User> findAll();

    /**
     * Checks if a user with the given UID/CID exists.
     *
     * @param uidcid the unique identifier of the user
     * @return the count of users with the given UID/CID
     */
    @Query("MATCH (u:User { uidcid: $uidcid }) RETURN count(u)")
    long checkUidcidExists(String uidcid);

    /**
     * Creates a new user.
     *
     * @param uidcid   the unique identifier of the user
     * @param nickName the nickname of the user
     * @return the created user
     */
    @Query("CREATE (u:User { uidcid: $uidcid, nick_name: $nickName }) RETURN u")
    User createUser(@Param("uidcid") String uidcid,
                    @Param("nickName") String nickName);

    /**
     * Updates the nickname of a user.
     *
     * @param uidcid   the unique identifier of the user
     * @param nickName the new nickname of the user
     * @return the updated user
     */
    @Query("MATCH (u:User { uidcid: $uidcid }) SET u.nick_name = $nickName RETURN u")
    User updateUser(@Param("uidcid") String uidcid,
                    @Param("nickName") String nickName);

    /**
     * Deletes users by their UID/CIDs.
     *
     * @param uidcids the list of UID/CIDs of the users to be deleted
     */
    @Query("MATCH (u:User) WHERE u.uidcid IN $uidcids DETACH DELETE u")
    void deleteByUidcids(List<String> uidcids);

    /**
     * Retrieves the UUIDs of graphs associated with the given users.
     *
     * @param uidcids the list of UID/CIDs of the users
     * @return the list of UUIDs of the graphs
     */
    @Query("MATCH (u:User) WHERE u.uidcid IN $uidcids "
            + "WITH u "
            + "MATCH (u)-[r:RELATION]->(g:Graph) "
            + "RETURN g.uuid")
    List<String> getGraphUuidsByUserUidcid(List<String> uidcids);
}
