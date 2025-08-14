package edu.example.service;

import edu.example.model.AuthSession;
import edu.example.model.User;
import edu.example.repository.AuthSessionRepository;
import edu.example.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.RollbackException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthSessionService {

    private static final int SESSION_LIFETIME_MINUTES = 30;

    private final AuthSessionRepository authSessionRepository;

    private final UserRepository userRepository;

    @Autowired
    public AuthSessionService(AuthSessionRepository authSessionRepository, UserRepository userRepository) {
        this.authSessionRepository = authSessionRepository;
        this.userRepository = userRepository;
    }

    public AuthSession createAuthSession(String username) {
        User user = userRepository.findUserByLogin(username).orElseThrow(EntityNotFoundException::new);
        try {
            authSessionRepository.save(new AuthSession(user, SESSION_LIFETIME_MINUTES));
        } catch (RollbackException e) {
            authSessionRepository.deleteAuthSessionByUser(user);
            authSessionRepository.save(new AuthSession(user, SESSION_LIFETIME_MINUTES));
        }
        return authSessionRepository.findAuthSessionByUser(user);
    }

}
