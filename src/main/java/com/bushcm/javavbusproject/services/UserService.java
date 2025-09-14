package com.bushcm.javavbusproject.services;

import com.bushcm.javavbusproject.entity.User;
import com.bushcm.javavbusproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Optional<User> findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public User registerUser(String username, String rawPassword, String fullName, String role) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(encoder.encode(rawPassword));
        u.setFullName(fullName);
        u.setRole(role);
        return repo.save(u);
    }

    public boolean checkPassword(User user, String rawPassword) {
        if (user == null) return false;
        try {
            return encoder.matches(rawPassword, user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }
}
