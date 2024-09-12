package com.paiondata.aristotle.service;

import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.impl.Neo4jServiceImpl;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.neo4j.driver.*;

@RunWith(MockitoJUnitRunner.class)
public class Neo4jServiceTest {

    @InjectMocks
    private Neo4jServiceImpl neo4jService;

    @Mock
    private GraphRepository graphRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Driver driver;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
}
