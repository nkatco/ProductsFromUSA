package com.example.productsfromusa.controllers;

import com.example.productsfromusa.models.Post;
import com.example.productsfromusa.services.data.CategoryService;
import com.example.productsfromusa.services.data.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/admin")
public class RestAdminController {

    private final PostService postService;

    @Value("${images.url}")
    String imagesUrl;

    @Autowired
    public RestAdminController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/image/{postId}")
    public Resource getImage(@PathVariable("postId") String postId) throws IOException {
        Post post = postService.getPostById(postId);
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }

        String imagePath = post.getImage();
        try {
            Path path = Paths.get(imagePath);
            if (Files.exists(path) && Files.isReadable(path)) {
                byte[] imageBytes = Files.readAllBytes(path);

                return new ByteArrayResource(imageBytes);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found", e);
        }
    }
}
