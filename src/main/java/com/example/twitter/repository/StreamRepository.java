package com.example.twitter.repository;

import com.example.twitter.model.Hilo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StreamRepository extends MongoRepository<Hilo, String> {
}
