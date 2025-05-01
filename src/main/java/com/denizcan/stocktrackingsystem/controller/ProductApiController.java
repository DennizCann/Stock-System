package com.denizcan.stocktrackingsystem.controller;

import com.denizcan.stocktrackingsystem.model.Product;
import com.denizcan.stocktrackingsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }
    
    // Tüm ürünleri getir
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // ID'ye göre ürün getir
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Yeni ürün ekle
    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // Ürün güncelle
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Optional<Product> existingProduct = productService.getProductById(id);
        if (existingProduct.isPresent()) {
            product.setId(id);
            Product updatedProduct = productService.updateProduct(product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Ürün sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Belirli bir miktarın altındaki ürünleri getir
    @GetMapping("/low-stock/{quantity}")
    public ResponseEntity<List<Product>> findProductsBelowQuantity(@PathVariable int quantity) {
        List<Product> products = productService.findProductsBelowQuantity(quantity);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // İsme göre ürün ara
    @GetMapping("/search")
    public ResponseEntity<List<Product>> findProductsByName(@RequestParam String name) {
        List<Product> products = productService.findProductsByName(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Fiyat aralığına göre ürünleri getir
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> findProductsInPriceRange(
            @RequestParam Double minPrice, 
            @RequestParam Double maxPrice) {
        List<Product> products = productService.findProductsInPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Belirli bir kategorinin toplam envanter değerini hesapla
    @GetMapping("/inventory-value/{category}")
    public ResponseEntity<Double> calculateInventoryValueByCategory(@PathVariable String category) {
        Double value = productService.calculateInventoryValueByCategory(category);
        return new ResponseEntity<>(value != null ? value : 0.0, HttpStatus.OK);
    }
} 