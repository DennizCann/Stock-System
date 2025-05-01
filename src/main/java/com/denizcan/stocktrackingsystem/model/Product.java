package com.denizcan.stocktrackingsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    private Long id;
    
    private String name;
    
    private Integer quantity;
    
    private String description;
    
    // Ürünün fiyatı da eklenebilir ama basit tutuyoruz
    private Double price;

    // Ürün kategorisi
    private String category;
} 