package edu.example.repository;

import edu.example.model.Location;
import edu.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LocationRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public LocationRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void deleteLocation(String id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("delete from Location l where l.id = :id")
                    .setParameter("id", id).executeUpdate();
            tx.commit();
        }
    }

    public List<Location> findAllUserLocations(User user) {
        try (Session session = sessionFactory.openSession()) {
            return session.createSelectionQuery("from Location l where l.user = :user", Location.class)
                    .setParameter("user", user).getResultList();
        }
    }

    public void save(Location location) {
        sessionFactory.inTransaction(session -> {
            session.persist(location);
        });
    }

}
