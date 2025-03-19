package com.example.twitter.controller;

import com.example.twitter.model.Post;
import com.example.twitter.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class AppController {

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public Post createPost(@RequestBody Post post){

        return postService.createPost(post.getUserId(), post.getContent(), post.getParentPostId());
    }

    @GetMapping("/{id}/replies")
    public List<Post> getReplies(@PathVariable String id){
        return postService.getReplies(id);
    }

    @GetMapping("/stream")
    public List<Post> getRootStream(){
        return postService.getRootThreads();
    }

    @DeleteMapping("/{id}")
    public void deleteStream(@PathVariable String id){
        postService.deletePost(id);
    }

}
