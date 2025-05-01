package com.denizcan.stocktrackingsystem.repository;

import com.denizcan.stocktrackingsystem.model.Product;
import com.denizcan.stocktrackingsystem.model.Category;
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

    // En az stoğu kalan X ürünü bulma (kritik stok ürünleri)
    @Query("SELECT p FROM Product p WHERE p.quantity > 0 ORDER BY p.quantity ASC")
    List<Product> findLowStockProducts(org.springframework.data.domain.Pageable pageable);
    
    // Kategoriye göre ürün bulma - Category nesnesi ile
    @Query(value = "SELECT * FROM product WHERE category_id = :#{#category.id}", nativeQuery = true)
    List<Product> findByCategory(Category category);
    
    // Kategori ID'sine göre ürün bulma - native SQL ile
    @Query(value = "SELECT * FROM product WHERE category_id = :categoryId", nativeQuery = true)
    List<Product> findByCategoryId(Long categoryId);

    // Kategori adına göre ürün bulma - native SQL sorgusu ile
    @Query(value = "SELECT * FROM product p JOIN category c ON p.category_id = c.id WHERE c.name = :categoryName", nativeQuery = true)
    List<Product> findByCategoryName(String categoryName);
    
    // Belirli bir kategorideki toplam stok değerini hesaplama - Kategori nesnesine göre
    @Query(value = "SELECT SUM(price * quantity) FROM product WHERE category_id = :#{#category.id}", nativeQuery = true)
    Double calculateInventoryValueByCategory(Category category);
    
    // Belirli bir kategorideki toplam stok değerini hesaplama - Kategori adına göre
    @Query(value = "SELECT SUM(p.price * p.quantity) FROM product p JOIN category c ON p.category_id = c.id WHERE c.name = :categoryName", nativeQuery = true)
    Double calculateInventoryValueByCategoryName(String categoryName);

    @Query("SELECT p.id FROM Product p")
    List<Long> findAllIds();
} 