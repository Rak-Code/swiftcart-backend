package com.ecommerce.project.service;

import com.ecommerce.project.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    Category createCategory(String name);

    Category getCategory(String categoryId);

    List<Category> getAllCategories();

    Page<Category> getAllCategories(Pageable pageable);

    Category updateCategory(String categoryId, String name);

    void deleteCategory(String categoryId);
}
