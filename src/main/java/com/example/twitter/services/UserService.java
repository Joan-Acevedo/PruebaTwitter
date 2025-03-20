package com.example.twitter.services;


import com.example.twitter.model.User;
import com.example.twitter.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Encriptar la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
         return userRepository.findByUsername(username).isPresent();
    }

}
