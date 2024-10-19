---
sidebar_position: 5
title: Filtering
---

# Filtering in Aristotle WS

Aristotle WS provides powerful filtering capabilities, allowing users to query nodes in the knowledge graph based on specific criteria. This feature enhances data retrieval by enabling targeted searches, thus improving the performance and relevance of results.

## How Filters Are Implemented

The filtering functionality is implemented through a Data Transfer Object (DTO) called `FilterQueryGraphDTO`. It includes two main components:

1. **uuid**: A unique identifier for the graph, indicating the specific graph to query.
2. **properties**: A `Map<String, String>` that represents the filtering criteria. Each key-value pair's key corresponds to an attribute of the graph nodes, while the value represents the filtering value for that attribute.

For example, a user can specify the filtering criteria in the following format:

```json
{
  "uuid": "3e308cd7b15c46bea971b43e090b18d2",
  "properties": {
    "language": "En",
    "status": "false"
  }
}
```

The above request will filter for nodes in the graph where `language` is "En" and `status` is "false".

## Details of Filtering Logic Implementation

In the service layer, the business logic processes this filtering through the `getRelationByGraphUuid` method. This method dynamically constructs the Cypher query based on the provided `uuid` and `properties`:

```java
final String cypherQuery = "MATCH (g1:Graph { uuid: $uuid }) "
        + "OPTIONAL MATCH (g1)-[:RELATION]->(n1:GraphNode) "
        + (properties != null && !properties.isEmpty() ?
        getFilterProperties(Constants.NODE_ALIAS_N1, properties.entrySet()) : "")
        + " OPTIONAL MATCH (n1)-[r:RELATION]->(n2:GraphNode) "
        + (properties != null && !properties.isEmpty() ?
        getFilterProperties(Constants.NODE_ALIAS_N2, properties.entrySet()) : "")
        + " RETURN DISTINCT n1, r, n2";
```

Here, the `getFilterProperties` method converts the filtering criteria into appropriate Cypher query snippets. For instance, if `properties` contains `{ "language": "En", "Status": "false" }`, the generated Cypher query will be similar to:

```cypher
MATCH (g1:Graph { uuid: $uuid }) 
OPTIONAL MATCH (g1)-[:RELATION]->(n1:GraphNode { language: 'En', Status: 'false' }) 
OPTIONAL MATCH (n1)-[r:RELATION]->(n2:GraphNode { language: 'En', Status: 'false' }) 
RETURN DISTINCT n1, r, n2
```

If `properties` is empty or not provided, the query does not perform additional filtering and returns all nodes that meet the criteria.

## How to Pass Filter Conditions

When sending a filtering request to Aristotle WS, the user must use the POST method and include the graph's `uuid` and `properties` in the request body. For example:

POST /graph
Content-Type: application/json
```json
{
  "uuid": "3e308cd7b15c46bea971b43e090b18d2",
  "properties": {
  "language": "En",
  "Status": "false"
  }
}
```

### Parameter Descriptions

1. **uuid**: Required, specifies the unique identifier for the graph to query.
2. **properties**: Optional, a JSON object representing the filtering conditions as key-value pairs that match node attributes. Multiple attributes can be specified to combine filters.

### Response Example
If the filtering is successful, the service will return the nodes and relationships that match the criteria. For example, a possible response could look like this:

```json
{
  "code": 200,
  "msg": "Successful operation",
  "data": {
    "uuid": "3e308cd7b15c46bea971b43e090b18d2",
    "title": "Language Graph",
    "description": "Language related graph",
    "createTime": "2024-10-19 16:07:26",
    "updateTime": "2024-10-19 16:07:26",
    "nodes": [
      {
        "uuid": "2ab78d7c532b41cda028084fd8a5cdd3",
        "properties": {
          "Status": "false",
          "language": "En",
          "exercitatione9": "reprehenderit tempor minim ad qui"
        },
        "createTime": "2024-10-19 16:07:26",
        "updateTime": "2024-10-19 16:07:26"
      }
    ],
    "relations": []
  }
}
```

## Use Cases for Filtering Functionality

- **UPrecise Data Selection**: The filtering functionality allows quick selection of specific nodes within large graphs, such as filtering active or inactive users or specific language content.
- **Multi-condition Combination Queries**: The `properties` object supports the combination of multiple conditions, enabling users to filter nodes based on various attributes simultaneously, such as language and status.
- **Default Return of All Data**: If no `properties` are provided or an empty object is passed, the system will return all nodes and relationship data.
