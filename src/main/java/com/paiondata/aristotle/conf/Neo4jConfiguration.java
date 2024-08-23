package com.paiondata.aristotle.conf;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Neo4jConfiguration {

    @Bean
    public SessionFactory sessionFactory() {
        Configuration configuration = new Configuration.Builder()
                .uri("bolt://localhost:7688")
                .credentials("neo4j", "12345678")
                .build();

        return new SessionFactory(configuration, "com.paiondata.aristotle.model.entity");
    }
}