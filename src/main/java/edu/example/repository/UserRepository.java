package edu.example.repository;

import edu.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<User> findUserByLogin(String login) {
        User user;
        try (Session session = sessionFactory.openSession()) {
            user = session.createSelectionQuery("from User where login like :login", User.class)
                    .setParameter("login", login)
                    .getSingleResultOrNull();
        }
        return Optional.ofNullable(user);
    }

    public List<String> findAllLogins() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select u.login from User u", String.class).getResultList();
        }
    }

    public void save(User user) {
        sessionFactory.inTransaction(session -> {
            session.persist(user);
        });
    }

}
