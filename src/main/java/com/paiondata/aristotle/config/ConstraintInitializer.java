package com.paiondata.aristotle.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;

@Component
public class ConstraintInitializer implements CommandLineRunner {

    @Autowired
    private Driver neo4jDriver;

    @Override
    public void run(String... args) {
        try (Session session = neo4jDriver.session(SessionConfig.builder().build())) {
            session.writeTransaction(tx -> {
                tx.run("CREATE CONSTRAINT IF NOT EXISTS FOR (u:User) REQUIRE u.uidcid IS UNIQUE");
                tx.run("CREATE CONSTRAINT IF NOT EXISTS FOR (g:Graph) REQUIRE g.uuid IS UNIQUE");
                tx.run("CREATE CONSTRAINT IF NOT EXISTS FOR (gn:GraphNode) REQUIRE gn.uuid IS UNIQUE");
                return null;
            });
        }
    }
}