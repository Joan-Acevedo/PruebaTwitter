package com.example.twitter.controller;

import com.example.twitter.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

import com.example.twitter.model.User;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder; // Inyecta el encoder
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "El usuario ya existe"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hashea la contraseña
        userService.registerUser(user);

        return ResponseEntity.ok(Map.of("message", "Registro exitoso"));
    }


}
