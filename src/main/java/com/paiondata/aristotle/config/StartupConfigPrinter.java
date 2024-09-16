package com.paiondata.aristotle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupConfigPrinter implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${spring.neo4j.uri}")
    private String neo4jUri;

    @Value("${spring.neo4j.authentication.username}")
    private String neo4jUsername;

    @Value("${spring.neo4j.authentication.password}")
    private String neo4jPassword;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("Neo4j URI: " + neo4jUri);
        System.out.println("Neo4j Username: " + neo4jUsername);
        // 注意密码不应该轻易打印到控制台或日志文件中，这里只是为了演示
        System.out.println("Neo4j Password: " + neo4jPassword);
    }
}