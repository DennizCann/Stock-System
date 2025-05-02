package com.denizcan.stocktrackingsystem.controller;

import com.denizcan.stocktrackingsystem.model.Product;
import com.denizcan.stocktrackingsystem.model.Category;
import com.denizcan.stocktrackingsystem.service.ProductService;
import com.denizcan.stocktrackingsystem.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/products")
    public String listProducts(Model model, @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String category, 
                             @RequestParam(required = false) String stockStatus) {
        // Mevcut ürünleri getir - burada filtreleme için geçici çözüm
        List<Product> products;
        
        if (keyword != null || category != null || stockStatus != null) {
            // Bu metot henüz tanımlanmamış, geçici olarak başka metodu kullanalım
            products = productService.getAllProducts(); // Tüm ürünleri al
            
            // Sonra manuel filtreleme yapalım
            List<Product> filteredProducts = new ArrayList<>();
            for (Product product : products) {
                boolean matches = true;
                
                // Anahtar kelime filtresi
                if (keyword != null && !keyword.isEmpty()) {
                    matches = product.getName() != null && 
                              product.getName().toLowerCase().contains(keyword.toLowerCase());
                }
                
                // Kategori filtresi
                if (matches && category != null && !category.isEmpty() && !category.equals("-- Kategori Seçin --")) {
                    matches = product.getCategory() != null && 
                              product.getCategory().toString().equals(category);
                }
                
                // Stok durum filtresi
                if (matches && stockStatus != null && !stockStatus.isEmpty() && !stockStatus.equals("-- Stok Durumu --")) {
                    if (stockStatus.equals("low")) {
                        matches = product.getQuantity() < 10; // Kritik stok seviyesi
                    } else if (stockStatus.equals("inStock")) {
                        matches = product.getQuantity() > 0; // Stokta var
                    } else if (stockStatus.equals("outOfStock")) {
                        matches = product.getQuantity() <= 0; // Stokta yok
                    }
                }
                
                if (matches) {
                    filteredProducts.add(product);
                }
            }
            
            products = filteredProducts;
        } else {
            products = productService.getAllProducts();
        }
        
        // Kategori isimlerini ayarla
        for (Product product : products) {
            if (product.getCategory() != null) {
                try {
                    // Eğer kategori bir ID ise
                    Long categoryId = Long.valueOf(product.getCategory().toString());
                    // Doğrudan repository kullan
                    Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
                    if (categoryOpt.isPresent()) {
                        // Kategori nesnesinden ismi al ve ayarla
                        product.setCategory(categoryOpt.get().getName());
                    }
                } catch (NumberFormatException e) {
                    // Zaten string ise birşey yapma
                }
            }
        }
        
        model.addAttribute("products", products);
        model.addAttribute("allCategories", categoryRepository.findAll());
        return "product-list";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll()); // Tüm kategorileri gönder
        return "add-product";
    }
    
    @PostMapping("/products/save")
    public String addProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }
} 