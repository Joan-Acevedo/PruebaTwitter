package com.example.twitter.services;

import com.example.twitter.model.User;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class UserService {

    private final MongoClient mongoClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public UserService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @PostConstruct
    public void init() {
        addUser(new User("andrea", "andrea"));
        addUser(new User("joan", "joan"));
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                User user = new User();
                user.setUsername(document.getString("username"));
                user.setPassword("************"); // No devolver contraseñas reales
                users.add(user);
            }
        }
        LOGGER.info("Getting users...");
        return users;
    }

    public Document addUser(User user) {
        Document document = new Document()
                .append("username", user.getUsername())
                .append("password", hashOfPassword(user.getPassword()));
        LOGGER.info("Adding user: {}", document);
        getCollection().insertOne(document);
        return document;
    }

    public boolean verifyPassword(String username, String password) {
        Document document = getCollection().find(new Document("username", username)).first();
        if (document != null) {
            String hashedPassword = document.getString("password");
            return hashedPassword.equals(hashOfPassword(password));
        }
        return false;
    }

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("quarkustwitter").getCollection("users");
    }

    private String hashOfPassword(String password) {
        try {
            MessageDigest mda = MessageDigest.getInstance("SHA-256");
            byte[] hash = mda.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error hashing password", e);
        }
        return null;
    }
}
