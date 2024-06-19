package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.Anons;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AnonsRepository extends CrudRepository<Anons, Long> {
    Anons findAnonsById(String id);
    Anons findAnonsByTokenId(String id);
    Anons findAnonsByUserId(String id);
    Set<Anons> findAllByUserId(String id);
    Set<Anons> findAllByTokenId(String id);
    Anons save(Anons anons);
    void deleteAnonsById(String id);
    void deleteAllByTokenId(String id);
    List<Anons> findAll();
    boolean existsById(String id);
}