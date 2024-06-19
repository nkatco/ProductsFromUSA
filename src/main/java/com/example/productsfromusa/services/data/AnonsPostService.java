package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.AnonsPost;
import com.example.productsfromusa.repositories.AnonsPostsRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnonsPostService implements AnonsPostServiceImpl {
    @Autowired
    private AnonsPostsRepository anonsPostsRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public boolean addAnons(AnonsPost anonsPost) {
        anonsPostsRepository.save(anonsPost);
        return existsById(anonsPost.getId());
    }
    public void removeAnonsPostsByAnonsId(String id) {
        anonsPostsRepository.deleteAllByAnonsId(id);
    }
    public AnonsPost getAnonsPostByAnonsId(String id) {
        return anonsPostsRepository.findAnonsPostByAnonsId(id);
    }

    public AnonsPost getAnonsById(String id) {
        return anonsPostsRepository.findAnonsPostById(id);
    }

    @Transactional
    public AnonsPost saveAnons(AnonsPost anonsPost) {
        return anonsPostsRepository.save(anonsPost);
    }
    @Transactional
    public AnonsPost mergeAnons(AnonsPost anonsPost) {
        return entityManager.merge(anonsPost);
    }
    @Transactional
    public void removeAnonsById(String id) {
        anonsPostsRepository.deleteAnonsPostById(id);
    }

    public List<AnonsPost> getAll() {
        return anonsPostsRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        return anonsPostsRepository.existsById(id);
    }
}
