package com.denizcan.stocktrackingsystem.repository;

import com.denizcan.stocktrackingsystem.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // İsim içeriğine göre kategorileri arama
    List<Category> findByNameContainingIgnoreCase(String name);

    // Kategoriyi ismine göre bulma metodu
    Optional<Category> findByName(String name);

    @Query("SELECT c.id FROM Category c")
    List<Long> findAllIds();
} 