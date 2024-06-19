package com.example.productsfromusa.controllers;

import com.example.productsfromusa.models.Category;
import com.example.productsfromusa.models.Post;
import com.example.productsfromusa.services.data.CategoryService;
import com.example.productsfromusa.services.data.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final PostService postService;
    private final CategoryService categoryService;

    @Value("${images.url}")
    String imagesUrl;

    @Autowired
    public AdminController(PostService postService, CategoryService categoryService) {
        this.postService = postService;
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String showAdminPage(Model model) {
        List<Post> posts = postService.getAll();
        model.addAttribute("posts", posts);
        return "admin";
    }

    @GetMapping("/add-post")
    public String showAddPostPage(Model model) {
        List<Category> categories = categoryService.getAll();
        model.addAttribute("categories", categories);
        return "add-post";
    }

    @GetMapping("/post/{id}")
    public String showPostDetails(@PathVariable("id") String postId, Model model) {
        Post post = postService.getPostById(postId);

        Path imagePath = Paths.get(imagesUrl, post.getImage());

        model.addAttribute("post", post);
        model.addAttribute("imagePath", imagePath);

        return "post-details";
    }

    @PostMapping("/add-post")
    public String addPost(@RequestParam("name") String name,
                          @RequestParam("text") String text,
                          @RequestParam("price") String price,
                          @RequestParam("oldPrice") String oldPrice,
                          @RequestParam("ref") String ref,
                          @RequestParam("image") MultipartFile image,
                          @RequestParam("category") String categoryId) {
        logger.info("Adding a new post with name: {}", name);

        String imageUrl = null;
        if (!image.isEmpty()) {
            try {
                String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path imagePath = Path.of(imagesUrl + filename);
                Files.createDirectories(imagePath.getParent());
                Files.copy(image.getInputStream(), imagePath);
                imageUrl = imagesUrl + filename;
                logger.info("Image uploaded successfully: {}", filename);
            } catch (IOException e) {
                logger.error("Failed to upload image", e);
            }
        }

        Category category = categoryService.getCategoryById(categoryId);

        Post post = new Post();
        Post post1 = postService.getLastPost(null);
        if (post1 != null) {
            post.setNumber(post1.getNumber() + 1);
        } else {
            post.setNumber(0);
        }
        post.setName(name);
        post.setText(text);
        post.setRef(ref);
        post.setOldPrice(Integer.parseInt(oldPrice));
        post.setPrice(Integer.parseInt(price));
        post.setImage(imageUrl);
        post.setCategory(category);

        postService.savePost(post);
        logger.info("Post added successfully with name: {}", name);

        return "redirect:/admin";
    }

    @GetMapping("/add-category")
    public String showAddCategoryPage(Model model) {
        return "add-category";
    }

    @PostMapping("/add-category")
    public String addCategory(@RequestParam("name") String name) {
        logger.info("Adding a new category with name: {}", name);

        Category category = new Category();
        Category category1 = categoryService.getLastCategory();
        if(category1 != null) {
            category.setNumber(category1.getNumber() + 1);
        } else {
            category.setNumber(1);
        }
        category.setName(name);
        categoryService.saveCategory(category);
        logger.info("Category added successfully with name: {}", name);

        return "redirect:/admin";
    }
}
