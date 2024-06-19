package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
    Post findPostById(String id);
    Post findPostByNumber(int number);
    Post save(Post post);
    void deletePostById(String id);
    List<Post> findAll();
    boolean existsById(String id);
}
