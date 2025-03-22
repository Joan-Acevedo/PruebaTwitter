package com.example.twitter.services;

import com.example.twitter.model.User;
import com.example.twitter.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String authUser(String username, String password) {
        if (!userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario no existe");
        }

        User user = userRepository.findByUsername(username).get();
        System.out.println(user.getId());

        if (!passwordEncoder.matches("123qwe", user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return this.jwtService.sign(user.getId(), user.getUsername());
    }

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword().strip()));

        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

}
