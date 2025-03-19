package com.example.twitter.services;

import com.example.twitter.model.Post;
import com.example.twitter.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository p){
        postRepository = p;
    }

    public Post createPost(Post post){
        post.setCreationDate(LocalDate.now());
        return postRepository.save(post);
    }

    public List<Post> getAllPost(){
        return postRepository.findAll();
    }
}
