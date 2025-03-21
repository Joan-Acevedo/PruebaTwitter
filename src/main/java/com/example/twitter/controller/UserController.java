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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
