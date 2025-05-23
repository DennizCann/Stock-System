package com.denizcan.stocktrackingsystem.controller;

import com.denizcan.stocktrackingsystem.model.Product;
import com.denizcan.stocktrackingsystem.model.StockMovement;
import com.denizcan.stocktrackingsystem.service.ProductService;
import com.denizcan.stocktrackingsystem.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;
    private final ProductService productService;

    @Autowired
    public StockMovementController(StockMovementService stockMovementService, ProductService productService) {
        this.stockMovementService = stockMovementService;
        this.productService = productService;
    }
    
    // Tüm stok hareketlerini listele
    @GetMapping
    public String listStockMovements(@RequestParam(value = "movementType", required = false) String movementType, Model model) {
        List<StockMovement> movements;
        if (movementType != null && !movementType.isBlank()) {
            movements = stockMovementService.getStockMovementsByType(movementType);
        } else {
            movements = stockMovementService.getAllStockMovements();
        }
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
            stockMovementService.addStockMovement(movement);
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
        
        List<StockMovement> movements = stockMovementService.getStockMovementsByProduct(productId);
        
        model.addAttribute("product", product);
        model.addAttribute("movements", movements);
        
        return "stock-movements/product-movements";
    }
} 