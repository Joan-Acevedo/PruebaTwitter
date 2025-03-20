package com.example.twitter.controller;

import com.example.twitter.model.User;
import com.example.twitter.model.UserDTO;
import com.example.twitter.services.UserService;
import com.example.twitter.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty()) {
            response.put("error", "Usuario y contraseña son obligatorios.");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.existsByUsername(user.getUsername())) {
            response.put("error", "El nombre de usuario ya está en uso.");
            return ResponseEntity.badRequest().body(response);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.createUser(user);

        response.put("message", "Usuario registrado exitosamente.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();

        Optional<User> userOptional = userService.findByUsername(userDTO.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(user.getUsername());
                response.put("token", token);
                return ResponseEntity.ok(response);
            }
        }

        response.put("error", "Credenciales inválidas.");
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserData(@RequestHeader("Authorization") String token) {
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        Optional<User> user = userService.findByUsername(username);

        return user.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado."));
    }
}
