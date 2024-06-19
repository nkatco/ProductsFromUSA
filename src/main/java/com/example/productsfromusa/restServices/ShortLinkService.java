package com.example.productsfromusa.restServices;

import com.example.productsfromusa.models.ShortLink;
import com.example.productsfromusa.repositories.ShortLinkRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ShortLinkService implements ShortLinkServiceImpl {
    @Autowired
    private ShortLinkRepository shortLinkRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public boolean addShortLink(ShortLink shortLink) {
        shortLinkRepository.save(shortLink);
        return existsById(shortLink.getId());
    }

    public ShortLink getShortLinkById(String id) {
        return shortLinkRepository.findShortLinkById(id);
    }

    @Transactional
    public ShortLink saveShortLink(ShortLink shortLink) {
        return shortLinkRepository.save(shortLink);
    }
    @Transactional
    public ShortLink mergeShortLink(ShortLink shortLink) {
        return entityManager.merge(shortLink);
    }
    @Transactional
    public void removeShortLinkById(String id) {
        shortLinkRepository.deleteShortLinkById(id);
    }

    public List<ShortLink> getAll() {
        return shortLinkRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        return shortLinkRepository.existsById(id);
    }
}

