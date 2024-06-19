package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.ShortLink;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortLinkRepository extends CrudRepository<ShortLink, Long> {
    ShortLink findShortLinkById(String id);
    ShortLink save(ShortLink shortLink);
    void deleteShortLinkById(String id);
    List<ShortLink> findAll();
    boolean existsById(String id);
}
