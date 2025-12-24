package com.ecommerce.project.service;

import com.ecommerce.project.entity.Category;
import com.ecommerce.project.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(String name) {

        Category category = new Category();
        category.setName(name);

        Category saved = categoryRepository.save(category);
        log.info("Created category with ID: {}", saved.getId());
        
        return saved;
    }

    @Override
    public Category getCategory(String categoryId) {
        log.info("Fetching category with ID: {}", categoryId);
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAll();
    }

    @Override
    public Page<Category> getAllCategories(Pageable pageable) {
        log.info("Fetching all categories with pagination");
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category updateCategory(String categoryId, String name) {

        Category category = getCategory(categoryId);
        category.setName(name);

        Category updated = categoryRepository.save(category);
        log.info("Updated category {}", categoryId);
        
        return updated;
    }

    @Override
    public void deleteCategory(String categoryId) {
        categoryRepository.deleteById(categoryId);
        log.info("Deleted category {}", categoryId);
    }
}
