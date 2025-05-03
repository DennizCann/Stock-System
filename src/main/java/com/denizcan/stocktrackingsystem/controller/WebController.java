package com.denizcan.stocktrackingsystem.controller;

import com.denizcan.stocktrackingsystem.model.Category;
import com.denizcan.stocktrackingsystem.model.Product;
import com.denizcan.stocktrackingsystem.service.CategoryService;
import com.denizcan.stocktrackingsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Controller
public class WebController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Autowired
    public WebController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // Ana sayfa - Dashboard
    @GetMapping("/")
    public String home(Model model) {
        try {
            List<Product> allProducts = productService.getAllProducts();
            model.addAttribute("products", allProducts);
            model.addAttribute("productCount", allProducts.size());
            model.addAttribute("categoryCount", categoryService.getAllCategories().size());
            
            // Kritik stok ürünleri (10'dan az olan)
            List<Product> lowStockProducts = productService.findProductsBelowQuantity(10);
            model.addAttribute("lowStockProducts", lowStockProducts != null ? lowStockProducts : new ArrayList<>());
            
            // Toplam stok değerini hesaplama
            double totalValue = allProducts.stream()
                    .mapToDouble(p -> p.getPrice() * p.getQuantity())
                    .sum();
            model.addAttribute("totalValue", totalValue);
            
            return "index";
        } catch (Exception e) {
            // Hata loglanabilir
            e.printStackTrace();
            // Alternatif olarak ürünler sayfasına yönlendir
            return "redirect:/products";
        }
    }

    // Ürün düzenleme sayfası
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        productService.getProductById(id).ifPresent(product -> model.addAttribute("product", product));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "edit-product";
    }

    // Ürün güncelleme işlemi
    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Product product) {
        // Form gönderildiğinde ürünü doğrudan güncelle
        productService.updateProduct(product);
        return "redirect:/products";
    }

    // Ürün silme işlemi
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    // Kategori listesi sayfası
    @GetMapping("/categories")
    public String categoryList(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category-list";
    }

    // Kategori ekleme sayfası
    @GetMapping("/categories/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "add-category";
    }

    // Kategori ekleme işlemi
    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute Category category) {
        categoryService.saveCategory(category);
        return "redirect:/categories";
    }

    // Kategori düzenleme sayfası
    @GetMapping("/categories/edit/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        categoryService.getCategoryById(id).ifPresent(category -> model.addAttribute("category", category));
        return "edit-category";
    }

    // Kategori güncelleme işlemi
    @PostMapping("/categories/update")
    public String updateCategory(@ModelAttribute Category category) {
        categoryService.updateCategory(category);
        return "redirect:/categories";
    }

    // Kategori silme işlemi
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
    
    // Stok raporu sayfası
    @GetMapping("/reports/stock")
    public String stockReport(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("lowStockProducts", productService.findProductsBelowQuantity(10));
        return "stock-report";
    }

    // Hızlı stok artırma
    @GetMapping("/products/stock/{id}/increase")
    public String increaseStock(@PathVariable Long id) {
        productService.getProductById(id).ifPresent(product -> {
            product.setQuantity(product.getQuantity() + 1);
            productService.updateProduct(product);
        });
        return "redirect:/products";
    }

    // Hızlı stok azaltma
    @GetMapping("/products/stock/{id}/decrease")
    public String decreaseStock(@PathVariable Long id) {
        productService.getProductById(id).ifPresent(product -> {
            if (product.getQuantity() > 0) {
                product.setQuantity(product.getQuantity() - 1);
                productService.updateProduct(product);
            }
        });
        return "redirect:/products";
    }

    // Erişim reddedildi sayfası
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
} 