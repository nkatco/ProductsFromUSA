package com.example.productsfromusa.services;

import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.AnonsPostService;
import com.example.productsfromusa.services.data.AnonsService;
import com.example.productsfromusa.services.data.LastPostService;
import com.example.productsfromusa.services.data.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ScanAnonsService {
    @Autowired
    public PostService postService;
    @Autowired
    public AnonsPostService anonsPostService;
    @Autowired
    public LastPostService lastPostService;
    @Autowired
    public AnonsService anonsService;

    public Anons getAnonsPostForAnons(Anons anons, AnonsPost anonsPost, Set<AnonsPost> anonsPostSet) {
        try {
            Post post = anonsPost.getPost();
            if(post == null) {
                LastPost lastPost = anons.getLastPost();
                if(lastPost == null) {
                    lastPost = new LastPost();
                    lastPost.setPost(postService.getLastPost(anons.getCategory()));
                    if(lastPost.getPost() == null) {
                        return null;
                    }
                    post = postService.getLastPost(anons.getCategory());
                } else {
                    boolean a = true;
                    int b = 1;
                    while(a) {
                        post = postService.getPostByNumber(lastPost.getPost().getNumber() + b);
                        if(post == null) {
                            return null;
                        } else {
                            if(!post.getCategory().getId().equals(anons.getCategory().getId())) {
                                b++;
                            } else {
                                a = false;
                            }
                        }
                    }
                    lastPost.setPost(post);
                }
                if(lastPost.getId() != null) {
                    anons.setLastPost(null);
                    anonsService.saveAnons(anons);
                }
                post = lastPost.getPost();
                Set<Post> posts = new HashSet<>();
                Set<Token> tokens = anons.getToken().getUser().getTokens();
                for(Token token : tokens) {
                    for(Anons anons1 : token.getAnonsList()) {
                        for (AnonsPost anonsPost1 : anons1.getAnonsPosts()) {
                            if(anonsPost1.getPost() != null) {
                                posts.add(anonsPost1.getPost());
                            }
                        }
                    }
                }
                for(Post post1 : posts) {
                    if(post1.getId().equals(post.getId())) {
                        return null;
                    }
                }
                lastPost.setPost(post);
                LastPost lastPost1 = lastPostService.saveLastPost(lastPost);
                anons.setLastPost(lastPost);

                System.out.println("Found a post for " + anons.getToken().getName() + " in " + anons.getDate());
                anonsPost.setPost(lastPost1.getPost());
                anonsPostService.saveAnons(anonsPost);
                anons.setLastPost(lastPost1);

                return anons;
            } else {
                System.out.println("Post " + post.getNumber() + "for "  + anons.getToken().getName() + " was exists.");
                return anons;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
