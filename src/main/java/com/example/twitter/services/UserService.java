package com.example.twitter.services;


import com.example.twitter.model.User;
import com.example.twitter.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository u){
        userRepository = u;
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
