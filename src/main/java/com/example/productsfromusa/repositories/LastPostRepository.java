package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.LastPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LastPostRepository extends CrudRepository<LastPost, Long> {
    LastPost findLastPostById(String id);
    LastPost save(LastPost lastPost);
    void deleteLastPostById(String id);
    List<LastPost> findAll();
    boolean existsById(String id);
}
