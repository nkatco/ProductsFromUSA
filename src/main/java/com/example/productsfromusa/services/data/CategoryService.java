package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.Category;
import com.example.productsfromusa.models.Post;
import com.example.productsfromusa.repositories.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService implements CategoryServiceImpl {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public boolean addCategory(Category category) {
        categoryRepository.save(category);
        return existsById(category.getId());
    }

    public Category getCategoryById(String id) {
        return categoryRepository.findCategoryById(id);
    }

    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category mergeCategory(Category category) {
        return entityManager.merge(category);
    }

    @Transactional
    public void removeCategoryById(String id) {
        categoryRepository.deleteCategoryById(id);
    }

    public Category getLastCategory() {
        List<Category> list = getAll();
        Category maxCategory = null;
        int maxNumber = Integer.MIN_VALUE;

        for (Category category : list) {
            int currentNumber = category.getNumber();
            if (currentNumber > maxNumber) {
                maxNumber = currentNumber;
                maxCategory = category;
            }
        }

        return maxCategory;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        return categoryRepository.existsById(id);
    }
}