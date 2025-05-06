package com.denizcan.stocktrackingsystem.service.impl;

import com.denizcan.stocktrackingsystem.model.Category;
import com.denizcan.stocktrackingsystem.model.Product;
import com.denizcan.stocktrackingsystem.repository.ProductRepository;
import com.denizcan.stocktrackingsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        System.out.println("Ürün siliniyor, ID: " + id);
        productRepository.deleteById(id);
        System.out.println("Ürün silindi.");
    }

    @Override
    public List<Product> findProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> findProductsBelowQuantity(Integer quantity) {
        return productRepository.findByQuantityLessThan(quantity);
    }

    @Override
    public List<Product> findProductsInPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public Double calculateInventoryValueByCategory(String categoryName) {
        return productRepository.calculateInventoryValueByCategoryName(categoryName);
    }

    @Override
    public List<Product> findProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }
} 