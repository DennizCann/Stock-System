package com.denizcan.stocktrackingsystem.service;

import com.denizcan.stocktrackingsystem.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    
    Category saveCategory(Category category);
    
    Optional<Category> getCategoryById(Long id);
    
    List<Category> getAllCategories();
    
    Category updateCategory(Category category);
    
    void deleteCategory(Long id);

    List<Category> findCategoriesByName(String name);
} 