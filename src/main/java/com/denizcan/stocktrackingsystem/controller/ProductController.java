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
    public String listProducts(Model model,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String stockStatus,
                               @RequestParam(required = false) Long categoryId) {

        // Tüm ürünleri al ve başlangıç durumunu logla
        List<Product> allProducts = productService.getAllProducts();
        System.out.println("=== KATEGORİ FİLTRELEME DEBUG BİLGİSİ ===");
        System.out.println("Toplam ürün sayısı: " + allProducts.size());

        // Kategori tiplerini logla - veritabanı durumunu anlamak için
        System.out.println("Ürünlerin kategori tipleri:");
        for (Product p : allProducts) {
            System.out.println("Ürün ID: " + p.getId() + ", Adı: " + p.getName());
            System.out.println("  Kategori: " + p.getCategory());
            System.out.println("  Kategori Sınıfı: " + (p.getCategory() == null ? "null" : p.getCategory().getClass().getName()));
        }

        // Eğer kategori filtrelemesi yapılıyorsa, parametreleri kontrol et
        if (categoryId != null) {
            System.out.println("Seçilen Kategori ID: " + categoryId);
            Optional<Category> selectedCategoryOpt = categoryRepository.findById(categoryId);
            if (selectedCategoryOpt.isPresent()) {
                Category selectedCategory = selectedCategoryOpt.get();
                System.out.println("Seçilen Kategori Adı: " + selectedCategory.getName());

                // Kategoriye göre filtrelemeyi manuel olarak dene
                List<Product> filteredByCategory = new ArrayList<>();
                for (Product p : allProducts) {
                    Object categoryValue = p.getCategory();
                    boolean match = false;

                    // Kategori değeri null kontrolü
                    if (categoryValue == null) {
                        System.out.println("Ürün " + p.getId() + " kategori değeri null");
                        continue;
                    }

                    // Kategori tipine göre karşılaştırma yap
                    if (categoryValue instanceof Category) {
                        Category productCategory = (Category) categoryValue;
                        match = productCategory.getId().equals(selectedCategory.getId());
                        System.out.println("Ürün " + p.getId() + " kategori ID karşılaştırma: " +
                                productCategory.getId() + " == " + selectedCategory.getId() + " : " + match);
                    } else if (categoryValue instanceof String) {
                        String categoryStr = (String) categoryValue;
                        // String değeri kategori ID'si olarak çevirip karşılaştır
                        try {
                            Long categoryIdFromStr = Long.parseLong(categoryStr);
                            match = categoryIdFromStr.equals(selectedCategory.getId());
                            System.out.println("Ürün " + p.getId() + " kategori ID karşılaştırma (String->Long): " +
                                    categoryIdFromStr + " == " + selectedCategory.getId() + " : " + match);
                        } catch (NumberFormatException e) {
                            // Eğer sayıya çevrilemezse, bu durumda kategori adı olarak karşılaştır
                            match = categoryStr.equalsIgnoreCase(selectedCategory.getName());
                            System.out.println("Ürün " + p.getId() + " kategori adı karşılaştırma: " +
                                    categoryStr + " == " + selectedCategory.getName() + " : " + match);
                        }
                    } else {
                        System.out.println("Ürün " + p.getId() + " bilinmeyen kategori tipi: " + categoryValue.getClass().getName());
                    }

                    if (match) {
                        filteredByCategory.add(p);
                        System.out.println("Ürün " + p.getId() + " filtreye eklendi");
                    }
                }

                System.out.println("Kategori filtresi sonrası kalan ürün sayısı: " + filteredByCategory.size());
                // Filtrelemeyi uygula ve diğer filtrelemelere devam et
                allProducts = filteredByCategory;
            } else {
                System.out.println("Seçilen kategori bulunamadı!");
            }
        }

        // Diğer filtrelemeler (stok, anahtar kelime vs.) buraya eklenecek...

        model.addAttribute("products", allProducts);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("stockStatus", stockStatus);

        return "product-list";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "add-product";
    }

    @PostMapping("/products/save")
    public String addProduct(@ModelAttribute Product product, @RequestParam("category.id") Long categoryId) {
        // Debug: id'nin null olup olmadığını kontrol et
        System.out.println("Yeni ürün ekleniyor, id: " + product.getId());
        // id burada null olmalı!
        Category category = categoryRepository.findById(categoryId).orElse(null);
        product.setCategory(category);
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("category.id") Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        product.setCategory(category);
        productService.updateProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id).orElseThrow();
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "edit-product";
    }
} 