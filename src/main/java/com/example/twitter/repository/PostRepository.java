package com.example.twitter.repository;

import com.example.twitter.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByParentPostId(String parentPostId);

    List<Post> findByUserId(String userId);
}
