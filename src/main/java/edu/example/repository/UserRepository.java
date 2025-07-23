package edu.example.repository;

import edu.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(User user) {
        sessionFactory.inTransaction(session -> {
            session.persist(user);
        });
    }

    public List<String> getAllLogins() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select u.login from User u", String.class).getResultList();
        }
    }

}
