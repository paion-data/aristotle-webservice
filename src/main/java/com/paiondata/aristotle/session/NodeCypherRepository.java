package com.paiondata.aristotle.session;

import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.common.utils.Neo4jUtil;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.entity.GraphNode;
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
public class NodeCypherRepository {

    private final Driver driver;

    /**
     * Constructs a Neo4jServiceImpl object with the specified Driver.
     * @param driver the Driver instance
     */
    @Autowired
    public NodeCypherRepository(final Driver driver) {
        this.driver = driver;
    }

    public GraphNode createNode(String graphUuid, String nodeUuid, String relationUuid,
                                String currentTime, NodeDTO nodeDTO) {
        StringBuilder setProperties = new StringBuilder();
        for (Map.Entry<String, String> entry : nodeDTO.getProperties().entrySet()) {
            setProperties.append(", ").append(entry.getKey()).append(": '").append(entry.getValue()).append("'");
        }

        final String cypherQuery = "MATCH (g:Graph) WHERE g.uuid = $graphUuid SET g.update_time = $currentTime "
                + "CREATE (gn:GraphNode{uuid:$nodeUuid "
                + setProperties
                + ",create_time:$currentTime,update_time:$currentTime}) "
                + "WITH g, gn "
                + "CREATE (g)-[r:RELATION {name: 'HAVE', uuid: $relationUuid, "
                + "create_time: $currentTime, update_time: $currentTime}]->(gn) "
                + "RETURN gn";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.writeTransaction(tx -> {
                var result = tx.run(cypherQuery, Values.parameters(
                                "graphUuid", graphUuid,
                                "nodeUuid", nodeUuid,
                                "currentTime", currentTime,
                                "relationUuid", relationUuid
                        )
                );

                final Record record = result.next();
                Map<String, Object> objectMap = Neo4jUtil.extractNode(record.get("gn"));

                return GraphNode.builder()
                        .id((Long) objectMap.get("id"))
                        .uuid((String) objectMap.get("uuid"))
                        .properties((Map<String, String>) objectMap.get(Constants.PROPERTIES))
                        .createTime((String) objectMap.get(Constants.CREATE_TIME))
                        .updateTime((String) objectMap.get(Constants.UPDATE_TIME))
                        .build();
            });
        }
    }
}
