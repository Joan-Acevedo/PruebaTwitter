package com.example.twitter.controller;

import com.example.twitter.services.JWTService;
import com.example.twitter.services.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.twitter.dto.LoginDTO;
import com.example.twitter.model.User;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Handles the login authentication process for users.
     * 
     * This endpoint authenticates a user based on the provided username and
     * password.
     * If authentication is successful, a session token is generated and returned.
     * 
     * @param loginDTO The data transfer object containing the user's credentials
     *                 (username and password)
     * @return ResponseEntity containing the session token if authentication is
     *         successful,
     *         or an error message with 401 Unauthorized status if credentials are
     *         invalid
     */
    @PostMapping("/log-in")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {

            String session = this.userService.authUser(loginDTO.getUsername(), loginDTO.getPassword());

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("session", session);

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect credentials");
        }
    }

    /**
     * Registers a new user in the system.
     * 
     * @param user the User object containing registration information
     * @return ResponseEntity containing the saved user and HTTP status CREATED
     *         (201) if successful
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
