package com.example.twitter.services;


import com.example.twitter.model.User;
import com.example.twitter.repository.UserRepository;
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

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User createUser(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

