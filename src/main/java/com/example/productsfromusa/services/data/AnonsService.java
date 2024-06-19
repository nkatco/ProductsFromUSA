package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.Anons;
import com.example.productsfromusa.repositories.AnonsRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AnonsService implements AnonsServiceImpl {
    @Autowired
    private AnonsRepository anonsRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public boolean addAnons(Anons anons) {
        anonsRepository.save(anons);
        return existsById(anons.getId());
    }

    public Anons getAnonsById(String id) {
        return anonsRepository.findAnonsById(id);
    }
    public void deleteAnonsByTokenId(String id) {
        anonsRepository.deleteAllByTokenId(id);
    }
    public Set<Anons> getAllAnonsByTokenId(String id) {
        return anonsRepository.findAllByTokenId(id);
    }
    public Anons getAnonsByTokenId(String id) {
        return anonsRepository.findAnonsByTokenId(id);
    }
    public Anons getAnonsByUserId(String id) {
        return anonsRepository.findAnonsByUserId(id);
    }
    public Set<Anons> getAllAnonsByUserId(String id) {
        return anonsRepository.findAllByUserId(id);
    }

    @Transactional
    public Anons saveAnons(Anons anons) {
        return anonsRepository.save(anons);
    }
    @Transactional
    public Anons mergeAnons(Anons anons) {
        return entityManager.merge(anons);
    }
    @Transactional
    public void removeAnonsById(String id) {
        anonsRepository.deleteAnonsById(id);
    }

    public List<Anons> getAll() {
        return anonsRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        return anonsRepository.existsById(id);
    }
}
