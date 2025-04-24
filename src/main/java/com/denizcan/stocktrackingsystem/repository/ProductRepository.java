package com.denizcan.stocktrackingsystem.repository;

import com.denizcan.stocktrackingsystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Ürünleri isme göre arama
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Ürünleri kategoriye göre arama
    List<Product> findByCategory(String category);
    
    // Belirli bir stok miktarından az olan ürünleri bulma
    List<Product> findByQuantityLessThan(Integer quantity);
    
    // Fiyat aralığına göre ürünleri bulma
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // En az stoğu kalan X ürünü bulma (kritik stok ürünleri)
    @Query("SELECT p FROM Product p WHERE p.quantity > 0 ORDER BY p.quantity ASC")
    List<Product> findLowStockProducts(org.springframework.data.domain.Pageable pageable);
    
    // Belirli bir kategorideki toplam stok değerini hesaplama
    @Query("SELECT SUM(p.price * p.quantity) FROM Product p WHERE p.category = :category")
    Double calculateTotalInventoryValueByCategory(String category);
} 