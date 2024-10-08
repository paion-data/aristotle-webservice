package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;

import org.neo4j.driver.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommonService {

    /**
     * Retrieves an optional user by UIDCID.
     *
     * @param uidcid the UIDCID of the user
     * @return an Optional containing the user if found, or empty otherwise
     */
    Optional<User> getUserByUidcid(String uidcid);

    /**
     * Retrieves a graph by its UUID.
     *
     * @param uuid the UUID of the graph
     *
     * @return an optional containing the graph if found, or empty if not found
     */
    Optional<Graph> getGraphByUuid(String uuid);

    /**
     * Retrieves users' associated graphs by UIDCID.
     *
     * @param uidcid the UIDCID of the user
     *
     * @return a list of maps containing user information and associated graphs
     */
    List<Map<String, Object>> getGraphsByUidcid(String uidcid);

    /**
     * Creates and binds a new graph using the provided DTO.
     *
     * @param graphCreateDTO the DTO containing details to create a new graph
     * @param tx the Neo4j transaction
     *
     * @return the created graph
     */
    Graph createAndBindGraph(GraphCreateDTO graphCreateDTO, Transaction tx);
}
