package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {

//    @Query("CREATE (u:User { uidcid: $uidcid, nick_name: $nickName })")
//    void createUser(String uidcid, String nickName);
}