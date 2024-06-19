package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.Admin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Long> {

    Optional<Admin> findByFullName(String fullName);
}
