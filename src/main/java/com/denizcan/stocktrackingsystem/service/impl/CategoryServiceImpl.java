package com.denizcan.stocktrackingsystem.service.impl;

import com.denizcan.stocktrackingsystem.model.Category;
import com.denizcan.stocktrackingsystem.repository.CategoryRepository;
import com.denizcan.stocktrackingsystem.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, JdbcTemplate jdbcTemplate) {
        this.categoryRepository = categoryRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Category saveCategory(Category category) {
        if (category.getId() == null) {
            Long nextId = findSmallestAvailableId();
            category.setId(nextId);
            
            // Veritabanı AUTO_INCREMENT değerini güncelle
            updateAutoIncrementValue();
        }
        return categoryRepository.save(category);
    }

    private Long findSmallestAvailableId() {
        List<Long> allIds = categoryRepository.findAllIds();
        
        if (allIds.isEmpty()) {
            return 1L;
        }
        
        Collections.sort(allIds);
        
        Long expectedId = 1L;
        for (Long id : allIds) {
            if (!id.equals(expectedId)) {
                return expectedId;
            }
            expectedId++;
        }
        
        return allIds.get(allIds.size() - 1) + 1;
    }

    private void updateAutoIncrementValue() {
        try {
            // Tüm kategorileri getir ve en büyük ID'yi bul
            List<Long> allIds = categoryRepository.findAllIds();
            Long maxId = allIds.isEmpty() ? 1L : Collections.max(allIds);
            
            // AUTO_INCREMENT değerini en büyük ID + 1 olarak ayarla
            jdbcTemplate.execute("ALTER TABLE category AUTO_INCREMENT = " + (maxId + 1));
        } catch (Exception e) {
            // Hata durumunda log tut, ancak uygulamanın çalışmasını engelleme
            System.err.println("AUTO_INCREMENT değeri güncellenirken hata oluştu: " + e.getMessage());
        }
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
        
        // Otomatik artış değerini en küçük uygun değere ayarla
        updateAutoIncrementValue();
    }

    @Override
    public List<Category> findCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }
} 