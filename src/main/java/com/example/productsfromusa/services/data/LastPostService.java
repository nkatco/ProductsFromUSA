package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.LastPost;
import com.example.productsfromusa.repositories.LastPostRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LastPostService implements AnonsServiceImpl {
    @Autowired
    private LastPostRepository lastPostRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public boolean addLastPost(LastPost lastPost) {
        lastPostRepository.save(lastPost);
        return existsById(lastPost.getId());
    }

    public LastPost getLastPostById(String id) {
        return lastPostRepository.findLastPostById(id);
    }

    @Transactional
    public LastPost saveLastPost(LastPost lastPost) {
        return lastPostRepository.save(lastPost);
    }
    @Transactional
    public LastPost mergeLastPost(LastPost lastPost) {
        return entityManager.merge(lastPost);
    }
    @Transactional
    public void removeLastPostById(String id) {
        lastPostRepository.deleteLastPostById(id);
    }

    public List<LastPost> getAll() {
        return lastPostRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        return lastPostRepository.existsById(id);
    }
}
