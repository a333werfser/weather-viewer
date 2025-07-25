package edu.example.service;

import edu.example.model.User;
import edu.example.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean areValidCredentials(String username, String password) {
        Optional<User> user = userRepository.findUserByLogin(username);
        if (user.isPresent()) {
            User targetUser = user.get();
            return isPasswordMatchHashed(password, targetUser.getPassword());
        } else {
            return false;
        }
    }

    public boolean doesUsernameAlreadyExist(String username) {
        List<String> logins = userRepository.findAllLogins();
        for (String savedLogin : logins) {
            if (savedLogin.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void register(User user) {
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean isPasswordMatchHashed(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }

}
