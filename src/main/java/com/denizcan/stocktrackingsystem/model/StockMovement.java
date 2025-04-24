package com.denizcan.stocktrackingsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    private Integer quantity;
    
    // IN veya OUT
    private String movementType;
    
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private String note;
    
    // Bu hareketin sonrası ürün stok durumu
    private Integer resultingStock;
} 