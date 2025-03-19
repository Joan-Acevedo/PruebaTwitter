package com.example.twitter.controller;

import com.example.twitter.model.Post;
import com.example.twitter.services.StreamService;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/stream")
public class StreamController {

    private final StreamService streamService;

    // Inyección de dependencias mediante constructor (Spring recomienda esta forma)
    public StreamController(StreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping
    public List<Post> getPosts() {
        return streamService.getStream();
    }

    @PostMapping
    public Document createPost(@RequestBody Post post) {
        return streamService.addPost(post);
    }
}
