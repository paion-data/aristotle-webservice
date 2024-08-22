package com.paiondata.aristotle;

import com.paiondata.aristotle.model.entity.Person;
import com.paiondata.aristotle.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootNeo4jApplicationTests {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void contextLoads() {
        Person person = new Person();
        person.setName("Doom");
        personRepository.save(person);
    }
}
