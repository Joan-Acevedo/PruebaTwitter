package com.example.twitter.services;

import com.example.twitter.model.Post;
import com.example.twitter.model.Thread;
import com.example.twitter.repository.PostRepository;
import com.example.twitter.repository.ThreadRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Service class for handling operations related to posts.
 */
@Service
public class PostService {
    private final PostRepository postRepository;
    private final ThreadRepository threadRepository;

    /**
     * Constructs a new PostService with the required repositories.
     *
     * @param postRepository   Repository for managing posts.
     * @param threadRepository Repository for managing threads.
     */
    public PostService(PostRepository postRepository, ThreadRepository threadRepository) {
        this.postRepository = postRepository;
        this.threadRepository = threadRepository;
    }

    /**
     * Creates a new post. If the post is a reply to an existing post, it will be
     * linked to the same thread.
     * Otherwise, a new thread will be created.
     *
     * @param userId       The ID of the user creating the post.
     * @param content      The content of the post.
     * @param parentPostId (Optional) The ID of the parent post if this is a reply.
     * @return The created post.
     * @throws IllegalArgumentException if the parent post is not found.
     */
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

    /**
     * Retrieves a random selection of up to 5 posts to display as a feed.
     *
     * @return A list of randomly selected posts.
     */
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

    /**
     * Creates a new thread for a given post.
     *
     * @param post The post to create a thread for.
     * @return The updated post with a thread ID assigned.
     */
    public Post createThread(Post post) {
        Thread thread = new Thread(post.getId());
        thread = threadRepository.save(thread);
        post.setThreadId(thread.getId());
        return postRepository.save(post);
    }

    /**
     * Retrieves all posts made by a specific user.
     *
     * @param userId The ID of the user whose posts are to be retrieved.
     * @return A list of posts made by the user.
     */
    public List<Post> getPostsByUser(String userId) {
        return postRepository.findByUserId(userId);
    }

    /**
     * Retrieves a specific post by its ID.
     *
     * @param id The ID of the post to retrieve.
     * @return An Optional containing the post if found, otherwise empty.
     */
    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    /**
     * Retrieves all replies to a given post.
     *
     * @param parentPostId The ID of the parent post.
     * @return A list of replies to the given post.
     */
    public List<Post> getReplies(String parentPostId) {
        return postRepository.findByParentPostId(parentPostId);
    }

    /**
     * Deletes a post by its ID.
     *
     * @param id The ID of the post to delete.
     */
    public void deletePost(String id) {
        postRepository.deleteById(id);
    }
}
