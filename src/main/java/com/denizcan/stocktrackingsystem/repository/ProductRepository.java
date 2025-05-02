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
    
    // Belirli bir stok miktarından az olan ürünleri bulma
    List<Product> findByQuantityLessThan(Integer quantity);
    
    // Fiyat aralığına göre ürünleri bulma
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // Belirli bir kategorideki toplam stok değerini hesaplama - Kategori adına göre
    @Query(value = "SELECT SUM(p.price * p.quantity) FROM product p JOIN category c ON p.category_id = c.id WHERE c.name = :categoryName", nativeQuery = true)
    Double calculateInventoryValueByCategoryName(String categoryName);

    @Query("SELECT p.id FROM Product p")
    List<Long> findAllIds();
} 