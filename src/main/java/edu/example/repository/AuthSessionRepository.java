package edu.example.repository;

import edu.example.model.AuthSession;
import edu.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class AuthSessionRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public AuthSessionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public AuthSession findAuthSessionByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            return session.createSelectionQuery("from AuthSession where user = :user", AuthSession.class)
                    .setParameter("user", user)
                    .getSingleResult();
        }
    }

    public AuthSession findAuthSessionById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createSelectionQuery("from AuthSession a join fetch a.user where a.id = :id", AuthSession.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    public void deleteAuthSessionByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("delete from AuthSession a where a.user = :user")
                    .setParameter("user", user).executeUpdate();
            tx.commit();
        }
    }

    public void deleteAuthSessionById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("delete from AuthSession a where a.id = :id")
                    .setParameter("id", id).executeUpdate();
            tx.commit();
        }
    }

    public void save(AuthSession authSession) {
        sessionFactory.inTransaction(session -> {
            session.persist(authSession);
        });
    }

}
