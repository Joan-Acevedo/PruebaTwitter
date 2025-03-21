package com.example.twitter.controller;

import com.example.twitter.model.Post;
import com.example.twitter.services.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class AppController {
    private final PostService postService;

    public AppController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody Post post) {
        try {
            Post newPost = postService.createPost(post.getUserId(), post.getContent(), post.getParentPostId());
            return ResponseEntity.ok(newPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getPostsByUser(@RequestParam String userId) {
        try {
            return ResponseEntity.ok(postService.getPostsByUser(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/feed")
    public ResponseEntity<?> getFeed() {
        // verify user
        try {
            return ResponseEntity.ok(postService.getFeed());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable String id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<Post>> getReplies(@PathVariable String id) {
        return ResponseEntity.ok(postService.getReplies(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id) {
        postService.deletePost(id);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @PostMapping("/thread")
    public ResponseEntity<?> createThread(@RequestBody Post post) {
        try {
            if (post.getParentPostId() != null) {
                return ResponseEntity.badRequest().body("Un hilo debe empezar con un post principal sin parentPostId.");
            }

            Post newThreadPost = postService.createThread(post);
            return ResponseEntity.ok(newThreadPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
