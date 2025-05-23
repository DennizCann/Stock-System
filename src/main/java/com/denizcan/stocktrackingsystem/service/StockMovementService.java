package com.denizcan.stocktrackingsystem.service;

import com.denizcan.stocktrackingsystem.model.Product;
import com.denizcan.stocktrackingsystem.model.StockMovement;
import com.denizcan.stocktrackingsystem.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductService productService;

    @Autowired
    public StockMovementService(StockMovementRepository stockMovementRepository, ProductService productService) {
        this.stockMovementRepository = stockMovementRepository;
        this.productService = productService;
    }

    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAll();
    }

    public List<StockMovement> getStockMovementsByType(String movementType) {
        return stockMovementRepository.findByMovementTypeOrderByTimestampDesc(movementType);
    }

    public List<StockMovement> getStockMovementsByProduct(Long productId) {
        return stockMovementRepository.findByProductIdOrderByTimestampDesc(productId);
    }

    @Transactional
    public StockMovement addStockMovement(StockMovement movement) {
        // Ürünü bul
        Product product = productService.getProductById(movement.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        // Mevcut stok miktarını al
        Integer currentStock = product.getQuantity();
        Integer quantity = movement.getQuantity();

        // Hareket tipine göre stok miktarını güncelle
        switch (movement.getMovementType()) {
            case "IN":
                product.setQuantity(currentStock + quantity);
                break;
            case "OUT":
                if (currentStock < quantity) {
                    throw new RuntimeException("Yetersiz stok! Mevcut: " + currentStock + ", İstenen: " + quantity);
                }
                product.setQuantity(currentStock - quantity);
                break;
            case "RETURN":
                product.setQuantity(currentStock + quantity);
                break;
            default:
                throw new RuntimeException("Geçersiz hareket tipi: " + movement.getMovementType());
        }

        // Ürünü güncelle
        productService.updateProduct(product);

        // Hareket sonrası kalan stok miktarını kaydet
        movement.setResultingStock(product.getQuantity());

        // Zaman damgası ekle
        movement.setTimestamp(LocalDateTime.now());

        // Stok hareketini kaydet
        return stockMovementRepository.save(movement);
    }

    public void deleteStockMovement(Long id) {
        stockMovementRepository.deleteById(id);
    }
} 