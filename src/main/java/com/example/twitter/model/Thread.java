package com.example.twitter.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "threads")
public class Thread {
    @Id
    private String id;

    private List<String> posts = new ArrayList<>();

    public Thread() {
    }

    public Thread(String firstPostId) {
        this.posts.add(firstPostId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getPosts() {
        return posts;
    }

    public void addPost(String postId) {
        this.posts.add(postId);
    }
}
