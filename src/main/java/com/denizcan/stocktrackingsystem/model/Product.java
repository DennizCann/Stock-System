package com.denizcan.stocktrackingsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private Integer quantity;
    
    private String description;
    
    // Ürünün fiyatı da eklenebilir ama basit tutuyoruz
    private Double price;

    // Ürün kategorisi
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
} 