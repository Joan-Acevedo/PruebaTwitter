package com.example.twitter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The ViewController class handles the routing for web page views in the
 * application.
 * It allows cross-origin requests from any origin through the CrossOrigin
 * annotation.
 */
@Controller
@CrossOrigin(origins = "*")
public class ViewController {

    /**
     * Maps the root path ("/") to the login page.
     * 
     * @return The name of the login view template
     */
    @GetMapping("/")
    public String login() {
        return "login";
    }

    /**
     * Maps the "/register" path to the registration page.
     * 
     * @return The name of the register view template
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * Maps the "/inicio" path to the main index page.
     * 
     * @return The name of the index view template
     */
    @GetMapping("/inicio")
    public String admin() {
        return "index";
    }
}