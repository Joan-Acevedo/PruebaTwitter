package com.example.twitter.services;

import com.example.twitter.model.Post;
import com.example.twitter.model.Thread;
import com.example.twitter.repository.PostRepository;
import com.example.twitter.repository.ThreadRepository;

import io.jsonwebtoken.lang.Collections;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final ThreadRepository threadRepository;

    public PostService(PostRepository postRepository, ThreadRepository threadRepository) {
        this.postRepository = postRepository;
        this.threadRepository = threadRepository;
    }

    public Post createPost(String userId, String content, String parentPostId) {
        Post post = new Post(userId, content);

        if (parentPostId != null) {
            Optional<Post> parentPost = postRepository.findById(parentPostId);
            if (parentPost.isPresent()) {
                post.setParentPostId(parentPostId);
                post.setThreadId(parentPost.get().getThreadId());
            } else {
                throw new IllegalArgumentException("Parent post not found");
            }
        } else {
            Thread thread = new Thread(post.getId());
            thread = threadRepository.save(thread);
            post.setThreadId(thread.getId());
        }

        return postRepository.save(post);
    }

    public List<Post> getFeed() {
        List<Post> posts = postRepository.findAll();
        List<Post> randomPosts = new ArrayList<>();
        Random random = new Random();
        int size = Math.min(5, posts.size());
        while (randomPosts.size() < size) {
            Post randomPost = posts.get(random.nextInt(posts.size()));
            if (!randomPosts.contains(randomPost)) {
                randomPosts.add(randomPost);
            }
        }
        return randomPosts;
    }

    public Post createThread(Post post) {
        Thread thread = new Thread(post.getId());
        thread = threadRepository.save(thread);
        post.setThreadId(thread.getId());
        return postRepository.save(post);
    }

    public List<Post> getPostsByUser(String userId) {
        return postRepository.findByUserId(userId);
    }

    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    public List<Post> getReplies(String parentPostId) {
        return postRepository.findByParentPostId(parentPostId);
    }

    public void deletePost(String id) {
        postRepository.deleteById(id);
    }
}
