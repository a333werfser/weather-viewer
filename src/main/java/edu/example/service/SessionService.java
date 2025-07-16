package edu.example.service;

import edu.example.model.Session;
import edu.example.model.User;
import edu.example.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class SessionService {

    private final List<Session> sessions = new ArrayList<>();

    public void addSession(Session session) {
        sessions.add(session);
    }

    public boolean sessionExists(String id) {
        for (Session session : sessions) {
            if (session.getSessionId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public User findSessionUser(Session session) {
        UserRepository userRepository = new UserRepository();
        return userRepository.findUserById(session.getUserId());
    }

}
