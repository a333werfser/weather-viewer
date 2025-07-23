package edu.example.service;

import edu.example.model.User;
import edu.example.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isUsernameAlreadyExist(String username) {
        List<String> logins = userRepository.getAllLogins();
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

}
