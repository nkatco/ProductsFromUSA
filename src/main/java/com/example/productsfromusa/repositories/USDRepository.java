package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.USD;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface USDRepository extends CrudRepository<USD, Long> {
}
