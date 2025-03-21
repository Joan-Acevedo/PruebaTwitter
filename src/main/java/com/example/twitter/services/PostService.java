package com.example.twitter.services;

import com.example.twitter.model.Post;
import com.example.twitter.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Create a new Post. If it doesnt have a father, it converts in a father of a
     * new Stream
     * 
     * @param userId       Users id who created the Post
     * @param content      Content of the Post
     * @param parentPostId Id of the Post that replied, can be null
     * @return a new Post
     */
    public Post createPost(String userId, String content, String parentPostId) {

        Post post = new Post(userId, content);

        if (parentPostId != null) {
            Optional<Post> parentPost = postRepository.findById(parentPostId);
            if (parentPost.isPresent()) {
                post.setParentPostId(parentPostId);
                post.setThreadRootId(parentPost.get().getThreadRootId());
            } else {
                throw new IllegalArgumentException("El post principal no existe");
            }
        } else {
            post.setThreadRootId(post.getId());
        }

        return postRepository.save(post);

    }

    /**
     * Obtain a post by id
     * 
     * @param id id of a post
     * @return A post
     */
    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    /**
     * Obtain all replies
     * 
     * @param parentPostId Id of the principal Post
     * @return a list od all replies
     */
    public List<Post> getReplies(String parentPostId) {
        return postRepository.findByParentPostId(parentPostId);
    }

    /**
     * Obtain all principal Post of a stream
     * 
     * @return a list of principal post
     */
    public List<Post> getRootThreads() {
        return postRepository.findByParentPostIdIsNull();
    }

    public void deletePost(String id) {
        List<Post> replies = postRepository.findByParentPostId(id);
        for (Post reply : replies) {
            deletePost(reply.getId());
        }
        postRepository.deleteById(id);
    }
}
