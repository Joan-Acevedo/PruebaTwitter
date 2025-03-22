package com.example.twitter.services;

import com.example.twitter.model.Post;
import com.example.twitter.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPostWithoutParent() {
        // Arrange
        String userId = "user123";
        String content = "Test post";
        Post newPost = new Post(userId, content);
        when(postRepository.save(any(Post.class))).thenReturn(newPost);

        // Act
        Post result = postService.createPost(userId, content, null);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(content, result.getContent());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPostWithParent() {
        // Arrange
        String userId = "user123";
        String content = "Test reply";
        String parentId = "post456";
        String rootId = "post789";

        Post parentPost = new Post(userId, "Parent post");
        parentPost.setId(parentId);

        Post replyPost = new Post(userId, content);
        replyPost.setParentPostId(parentId);

        when(postRepository.findById(parentId)).thenReturn(Optional.of(parentPost));
        when(postRepository.save(any(Post.class))).thenReturn(replyPost);

        // Act
        Post result = postService.createPost(userId, content, parentId);

        // Assert
        assertNotNull(result);
        assertEquals(parentId, result.getParentPostId());
        verify(postRepository, times(1)).findById(parentId);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void getReplies() {
        // Arrange
        String parentId = "post123";
        List<Post> expectedReplies = Arrays.asList(
                new Post("user1", "Reply 1"),
                new Post("user2", "Reply 2"));
        when(postRepository.findByParentPostId(parentId)).thenReturn(expectedReplies);

        // Act
        List<Post> result = postService.getReplies(parentId);

        // Assert
        assertEquals(2, result.size());
        verify(postRepository, times(1)).findByParentPostId(parentId);
    }

    @Test
    void deletePost() {
        // Arrange
        String postId = "post123";
        List<Post> replies = Arrays.asList(
                new Post("user1", "Reply 1"),
                new Post("user2", "Reply 2"));
        replies.get(0).setId("reply1");
        replies.get(1).setId("reply2");

        when(postRepository.findByParentPostId(postId)).thenReturn(replies);
        when(postRepository.findByParentPostId("reply1")).thenReturn(List.of());
        when(postRepository.findByParentPostId("reply2")).thenReturn(List.of());

        // Act
        postService.deletePost(postId);

        // Assert
        verify(postRepository, times(1)).findByParentPostId(postId);
        verify(postRepository, times(1)).findByParentPostId("reply1");
        verify(postRepository, times(1)).findByParentPostId("reply2");
        verify(postRepository, times(1)).deleteById(postId);
        verify(postRepository, times(1)).deleteById("reply1");
        verify(postRepository, times(1)).deleteById("reply2");
    }
}