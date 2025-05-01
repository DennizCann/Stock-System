package com.denizcan.stocktrackingsystem.service;

import com.denizcan.stocktrackingsystem.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    // Temel CRUD işlemleri
    Product saveProduct(Product product);
    
    Optional<Product> getProductById(Long id);
    
    List<Product> getAllProducts();
    
    Product updateProduct(Product product);
    
    void deleteProduct(Long id);
    
    // Arama ve filtreleme işlemleri
    List<Product> findProductsByName(String name);

    List<Product> findProductsBelowQuantity(Integer quantity);
    
    List<Product> findProductsInPriceRange(Double minPrice, Double maxPrice);
    
    // Kategoriye göre envanter değeri hesaplama
    Double calculateInventoryValueByCategory(String categoryName);
} 