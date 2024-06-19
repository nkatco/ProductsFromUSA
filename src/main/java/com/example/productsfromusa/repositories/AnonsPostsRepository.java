package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.AnonsPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnonsPostsRepository extends CrudRepository<AnonsPost, Long> {
    AnonsPost findAnonsPostById(String id);
    AnonsPost save(AnonsPost anonsPost);
    AnonsPost findAnonsPostByAnonsId(String id);
    void deleteAnonsPostById(String idd);
    void deleteAllByAnonsId(String id);
    List<AnonsPost> findAll();
    boolean existsById(String id);
}
