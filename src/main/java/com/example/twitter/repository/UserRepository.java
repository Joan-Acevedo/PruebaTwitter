package com.example.twitter.repository;

import com.example.twitter.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    List<User> findAllByUsernameContainingIgnoreCase(String username);

}
