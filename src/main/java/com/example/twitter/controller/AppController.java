package com.example.twitter.controller;

import com.example.twitter.model.Post;
import com.example.twitter.services.JWTService;
import com.example.twitter.services.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing posts in the application.
 * 
 * This controller provides endpoints for creating, retrieving, and deleting
 * posts,
 * as well as specialized operations like retrieving user feeds, post replies,
 * and
 * creating thread posts. Some endpoints require JWT authentication.
 * 
 * All endpoints are available under the "/posts" base path and support
 * cross-origin
 * requests from any domain.
 * 
 * @RestController Maps HTTP requests to handler methods
 * @RequestMapping "/posts" Base path for all endpoints in this controller
 * @CrossOrigin Allows requests from any origin
 */
@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class AppController {
    private final PostService postService;
    private final JWTService jwtService;

    public AppController(PostService postService, JWTService jwtService) {
        this.postService = postService;
        this.jwtService = jwtService;
    }

    /**
     * Creates a new post based on the provided Post object.
     * 
     * @param post The Post object containing userId, content, and optionally
     *             parentPostId
     * @return ResponseEntity containing the newly created Post if successful, or an
     *         error message if the operation fails
     * @throws IllegalArgumentException if the post parameters are invalid (handled
     *                                  within the method)
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody Post post) {
        try {
            Post newPost = postService.createPost(post.getUserId(), post.getContent(), post.getParentPostId());
            return ResponseEntity.ok(newPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves all posts created by a specific user.
     * 
     * @param userId The identifier of the user whose posts are to be retrieved
     * @return ResponseEntity containing a list of posts if the user exists,
     *         or a bad request with an error message if the user doesn't exist
     */
    @GetMapping("/user")
    public ResponseEntity<?> getPostsByUser(@RequestParam String userId) {
        try {
            return ResponseEntity.ok(postService.getPostsByUser(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves the feed of posts for an authenticated user.
     * 
     * This endpoint requires authentication via a JWT token in the Authorization
     * header.
     * The token must be in the format "Bearer [token]". The method validates the
     * token
     * and then returns the feed of posts if authentication is successful.
     *
     * @param authHeader The Authorization header containing the JWT token
     *                   (required)
     * @return A ResponseEntity containing either:
     *         - 200 OK with the feed of posts if authentication succeeds
     *         - 401 Unauthorized if the token is invalid, missing, or
     *         authentication fails
     *         - 400 Bad Request if there's an illegal argument
     * @throws IllegalArgumentException if there's an issue with the request
     *                                  parameters
     * @throws Exception                if there's any other error during processing
     */
    @GetMapping("/feed")
    public ResponseEntity<?> getFeed(@RequestHeader(value = "Authorization", required = true) String authHeader) {
        try {
            // Verify JWT token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
            }

            String token = authHeader.substring(7);

            if (!this.jwtService.verify(token)) {
                return ResponseEntity.status(401).body("Unauthorized: Invalid token");
            }

            return ResponseEntity.ok(postService.getFeed());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized: " + e.getMessage());
        }
    }

    /**
     * Retrieves a post by its unique identifier.
     * 
     * @param id The unique identifier of the post to retrieve
     * @return ResponseEntity containing the Post if found with status code 200
     *         (OK),
     *         or status code 404 (NOT FOUND) if no post exists with the given ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable String id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all replies for a specific post.
     * 
     * @param id The unique identifier of the post for which replies are requested
     * @return ResponseEntity containing a list of Post objects that are replies to
     *         the specified post
     */
    @GetMapping("/{id}/replies")
    public ResponseEntity<List<Post>> getReplies(@PathVariable String id) {
        return ResponseEntity.ok(postService.getReplies(id));
    }

    /**
     * Deletes a post with the specified ID.
     *
     * @param id The unique identifier of the post to be deleted
     * @return ResponseEntity with a success message if the post was deleted
     *         successfully
     * @throws RuntimeException if the post doesn't exist or couldn't be deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id) {
        postService.deletePost(id);
        return ResponseEntity.ok("Post deleted successfully");
    }

    /**
     * Creates a new thread starting with the given post.
     * A thread must start with a main post that doesn't have a parent post.
     *
     * @param post The post that will serve as the start of the thread.
     *             It should not have a parentPostId.
     * @return ResponseEntity containing the newly created thread post if
     *         successful,
     *         or an error message if the request is invalid
     * @throws IllegalArgumentException if the post contains invalid data
     *                                  or violates business rules
     */
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
