package com.denizcan.stocktrackingsystem.controller;

import com.denizcan.stocktrackingsystem.model.Product;
import com.denizcan.stocktrackingsystem.model.StockMovement;
import com.denizcan.stocktrackingsystem.repository.StockMovementRepository;
import com.denizcan.stocktrackingsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/stock-movements")
public class StockMovementController {

    @Autowired
    private StockMovementRepository stockMovementRepository;
    
    @Autowired
    private ProductService productService;
    
    // Tüm stok hareketlerini listele
    @GetMapping
    public String listAllMovements(Model model) {
        List<StockMovement> movements = stockMovementRepository.findAll();
        model.addAttribute("movements", movements);
        return "stock-movements/list";
    }
    
    // Yeni stok hareketi ekleme formu
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("movement", new StockMovement());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("movementTypes", new String[]{"IN", "OUT", "RETURN"});
        return "stock-movements/add";
    }
    
    // Stok hareketi kaydetme
    @PostMapping("/add")
    public String addMovement(@ModelAttribute StockMovement movement, RedirectAttributes redirectAttributes) {
        try {
            // Ürünü bul
            Product product = productService.getProductById(movement.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));
            
            // Mevcut stok miktarını al
            Integer currentStock = product.getQuantity();
            Integer quantity = movement.getQuantity();
            
            // Hareket tipine göre stok miktarını güncelle
            if ("IN".equals(movement.getMovementType())) {
                // Stok girişi
                product.setQuantity(currentStock + quantity);
            } else if ("OUT".equals(movement.getMovementType())) {
                // Stok çıkışı (satış)
                if (currentStock < quantity) {
                    redirectAttributes.addFlashAttribute("errorMessage", 
                            "Yetersiz stok! Mevcut: " + currentStock + ", İstenen: " + quantity);
                    return "redirect:/stock-movements/add";
                }
                product.setQuantity(currentStock - quantity);
            } else if ("RETURN".equals(movement.getMovementType())) {
                // İade (stok artışı)
                product.setQuantity(currentStock + quantity);
            }
            
            // Ürünü güncelle
            productService.updateProduct(product);
            
            // Hareket sonrası kalan stok miktarını kaydet
            movement.setResultingStock(product.getQuantity());
            
            // Zaman damgası ekle
            movement.setTimestamp(LocalDateTime.now());
            
            // Stok hareketini kaydet
            stockMovementRepository.save(movement);
            
            redirectAttributes.addFlashAttribute("successMessage", "Stok hareketi başarıyla kaydedildi.");
            return "redirect:/stock-movements";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hata: " + e.getMessage());
            return "redirect:/stock-movements/add";
        }
    }
    
    // Belirli bir ürünün stok hareketlerini görüntüle
    @GetMapping("/product/{productId}")
    public String getProductMovements(@PathVariable Long productId, Model model) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));
        
        List<StockMovement> movements = stockMovementRepository.findByProductIdOrderByTimestampDesc(productId);
        
        model.addAttribute("product", product);
        model.addAttribute("movements", movements);
        
        return "stock-movements/product-movements";
    }
} 