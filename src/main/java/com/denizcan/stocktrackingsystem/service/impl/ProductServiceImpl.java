package com.denizcan.stocktrackingsystem.service.impl;

import com.denizcan.stocktrackingsystem.model.Product;
import com.denizcan.stocktrackingsystem.repository.ProductRepository;
import com.denizcan.stocktrackingsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, JdbcTemplate jdbcTemplate) {
        this.productRepository = productRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Product saveProduct(Product product) {
        if (product.getId() == null) {
            Long nextId = findSmallestAvailableId();
            product.setId(nextId);
            
            // Veritabanı AUTO_INCREMENT değerini güncelle
            updateAutoIncrementValue();
        }
        return productRepository.save(product);
    }

    private Long findSmallestAvailableId() {
        List<Long> allIds = productRepository.findAllIds();
        
        System.out.println("Mevcut ürün ID'leri: " + allIds);
        
        if (allIds.isEmpty()) {
            System.out.println("Hiç ürün yok, ID=1 döndürülüyor");
            return 1L;
        }
        
        Collections.sort(allIds);
        System.out.println("Sıralanmış ID listesi: " + allIds);
        
        Long expectedId = 1L;
        for (Long id : allIds) {
            if (!id.equals(expectedId)) {
                System.out.println("Boşluk bulundu: Beklenen=" + expectedId + ", Mevcut=" + id);
                return expectedId;
            }
            expectedId++;
        }
        
        System.out.println("Boşluk bulunamadı, son ID + 1 döndürülüyor: " + (allIds.get(allIds.size() - 1) + 1));
        return allIds.get(allIds.size() - 1) + 1;
    }

    private void updateAutoIncrementValue() {
        try {
            // Tüm ürünleri getir ve en büyük ID'yi bul
            List<Long> allIds = productRepository.findAllIds();
            Long maxId = allIds.isEmpty() ? 1L : Collections.max(allIds);
            
            // AUTO_INCREMENT değerini en büyük ID + 1 olarak ayarla
            jdbcTemplate.execute("ALTER TABLE product AUTO_INCREMENT = " + (maxId + 1));
            
            // Log ekle
            System.out.println("Ürün AUTO_INCREMENT değeri " + (maxId + 1) + " olarak güncellendi.");
        } catch (Exception e) {
            // Hata durumunda log tut, ancak uygulamanın çalışmasını engelleme
            System.err.println("AUTO_INCREMENT değeri güncellenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
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
        
        // Otomatik artış değerini en küçük uygun değere ayarla
        updateAutoIncrementValue();
        System.out.println("Ürün silindi ve AUTO_INCREMENT güncellendi.");
    }

    @Override
    public List<Product> findProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> findProductsByCategory(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
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
    public List<Product> findLowStockProducts(int limit) {
        return productRepository.findLowStockProducts(PageRequest.of(0, limit));
    }

    @Override
    public Double calculateInventoryValueByCategory(String categoryName) {
        return productRepository.calculateInventoryValueByCategoryName(categoryName);
    }
} 