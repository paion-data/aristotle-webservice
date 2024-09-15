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

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {

    @Query("MATCH (u:User { uidcid: $uidcid }) RETURN u")
    User getUserByUidcid(String uidcid);

    @Query("MATCH (u:User) RETURN u")
    List<User> findAll();

    @Query("MATCH (u:User { uidcid: $uidcid }) RETURN count(u)")
    long checkUidcidExists(String uidcid);

    @Query("CREATE (u:User { uidcid: $uidcid, nick_name: $nickName }) RETURN u")
    User createUser(@Param("uidcid") String uidcid,
                    @Param("nickName") String nickName);

    @Query("MATCH (u:User { uidcid: $uidcid }) SET u.nick_name = $nickName RETURN u")
    User updateUser(@Param("uidcid") String uidcid,
                    @Param("nickName") String nickName);

    @Query("MATCH (u:User) WHERE u.uidcid IN $uidcids DETACH DELETE u")
    void deleteByUidcids(List<String> uidcids);

    @Query("MATCH (u:User) WHERE u.uidcid IN $uidcids "
            + "WITH u "
            + "MATCH (u)-[r:RELATION]->(g:Graph) "
            + "RETURN g.uuid")
    List<String> getGraphUuidsByUserUidcid(List<String> uidcids);
}