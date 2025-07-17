package edu.example.controller;

import edu.example.model.People;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PeopleController {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @PostMapping("/people")
    public void setPeople() {
        People people = new People();
        people.setName("John");

        sessionFactory.inTransaction(session -> {
            session.persist(people);
        });
    }

}
