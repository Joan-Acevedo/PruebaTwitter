package com.example.twitter.controller;

import com.example.twitter.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.twitter.model.User;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }
}
