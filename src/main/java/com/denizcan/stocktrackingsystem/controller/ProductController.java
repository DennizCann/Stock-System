package com.denizcan.stocktrackingsystem.controller;

import com.denizcan.stocktrackingsystem.model.Product;
import com.denizcan.stocktrackingsystem.model.Category;
import com.denizcan.stocktrackingsystem.service.ProductService;
import com.denizcan.stocktrackingsystem.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    // Web UI Endpoint'leri
    @GetMapping
    public String listProducts(Model model,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String stockStatus,
                             @RequestParam(required = false) Long categoryId) {
        List<Product> allProducts = productService.getAllProducts();
        
        if (categoryId != null) {
            Optional<Category> selectedCategoryOpt = categoryRepository.findById(categoryId);
            if (selectedCategoryOpt.isPresent()) {
                Category selectedCategory = selectedCategoryOpt.get();
                allProducts = allProducts.stream()
                    .filter(p -> p.getCategory() != null && 
                           p.getCategory().getId().equals(selectedCategory.getId()))
                    .toList();
            }
        }

        model.addAttribute("products", allProducts);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("stockStatus", stockStatus);

        return "product-list";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "add-product";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("category.id") Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Kategori bulunamadı!"));
        product.setCategory(category);
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("category.id") Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        product.setCategory(category);
        productService.updateProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id).orElseThrow();
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "edit-product";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products;
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productService.findProductsByName(keyword);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("keyword", keyword);
        return "product-list";
    }

    // API Endpoint'leri
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<Product>> getAllProductsApi() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductByIdApi(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Product> saveProductApi(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Product> updateProductApi(@PathVariable Long id, @RequestBody Product product) {
        Optional<Product> existingProduct = productService.getProductById(id);
        if (existingProduct.isPresent()) {
            product.setId(id);
            Product updatedProduct = productService.updateProduct(product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteProductApi(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/low-stock/{quantity}")
    @ResponseBody
    public ResponseEntity<List<Product>> findProductsBelowQuantityApi(@PathVariable int quantity) {
        List<Product> products = productService.findProductsBelowQuantity(quantity);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<Product>> findProductsByNameApi(@RequestParam String name) {
        List<Product> products = productService.findProductsByName(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/api/price-range")
    @ResponseBody
    public ResponseEntity<List<Product>> findProductsInPriceRangeApi(
            @RequestParam Double minPrice, 
            @RequestParam Double maxPrice) {
        List<Product> products = productService.findProductsInPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/api/inventory-value/{category}")
    @ResponseBody
    public ResponseEntity<Double> calculateInventoryValueByCategoryApi(@PathVariable String category) {
        Double value = productService.calculateInventoryValueByCategory(category);
        return new ResponseEntity<>(value != null ? value : 0.0, HttpStatus.OK);
    }
} 