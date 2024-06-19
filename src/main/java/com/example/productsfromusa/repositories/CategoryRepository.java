package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findCategoryById(String id);
    Category save(Category category);
    void deleteCategoryById(String id);
    List<Category> findAll();
    boolean existsById(String id);
}
