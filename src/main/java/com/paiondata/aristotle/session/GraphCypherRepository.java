package com.paiondata.aristotle.session;

import com.paiondata.aristotle.common.utils.Neo4jUtil;
import com.paiondata.aristotle.model.entity.Graph;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.SessionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Repository
@Transactional
public class GraphCypherRepository {

    private final Driver driver;

    /**
     * Constructs a Neo4jServiceImpl object with the specified Driver.
     * @param driver the Driver instance
     */
    @Autowired
    public GraphCypherRepository(final Driver driver) {
        this.driver = driver;
    }

    public Graph createGraph(String title, String description, String userUidcid,
                                           String graphUuid, String relationUuid, String currentTime) {
        final String cypherQuery = "MATCH (u:User) WHERE u.uidcid = $userUidcid "
                + "CREATE (g:Graph {uuid: $graphUuid, title: $title, description: $description, "
                + "create_time: $currentTime, update_time: $currentTime}) "
                + "WITH u, g "
                + "CREATE (u)-[r:RELATION {name: 'HAVE', uuid: $relationUuid, create_time: $currentTime, "
                + "update_time: $currentTime}]->(g) "
                + "RETURN g";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.writeTransaction(tx -> {
                var result = tx.run(cypherQuery, Values.parameters(
                                "title", title,
                                "description", description,
                                "userUidcid", userUidcid,
                                "graphUuid", graphUuid,
                                "currentTime", currentTime,
                                "relationUuid", relationUuid
                        )
                );

                final Record record = result.next();
                Map<String, Object> objectMap = Neo4jUtil.extractGraph(record.get("g"));

                return Graph.builder()
                        .id((Long) objectMap.get("id"))
                        .uuid((String) objectMap.get("uuid"))
                        .title((String) objectMap.get("title"))
                        .description((String) objectMap.get("description"))
                        .createTime((String) objectMap.get("createTime"))
                        .updateTime((String) objectMap.get("updateTime"))
                        .build();
            });
        }
    }
}
