package com.denizcan.stocktrackingsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // Bu kategori altındaki ürünler (isteğe bağlı)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonIgnore // Sonsuz döngüyü önlemek için
    private List<Product> products = new ArrayList<>();

    @Override
    public String toString() {
        return name; // ID yerine kategori ismini döndür
    }
} 