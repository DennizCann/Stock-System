package com.denizcan.stocktrackingsystem.config;

import com.denizcan.stocktrackingsystem.model.Category;
import com.denizcan.stocktrackingsystem.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

@Component
public class DataMigrationRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${app.data-migration.enabled:false}")
    private boolean migrationEnabled;

    @Override
    public void run(String... args) {
        if (!migrationEnabled) {
            return; // Veri taşıma devre dışıysa hiçbir şey yapma
        }

        // Veri tabanında otomatik kategori oluşumunu önlemek için kontrol ekliyoruz
        if (categoryRepository.findByName("10").isPresent()) {
            // Eğer "10" adlı kategori zaten varsa, hiçbir şey yapma
            return;
        }

        // Eski tablodan String kategorileri kontrol et
        try {
            // Önce sütunun var olup olmadığını kontrol et
            boolean columnExists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) > 0 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'product' AND COLUMN_NAME = 'category_id'",
                Boolean.class);

            // Sütun yoksa ekle
            if (!columnExists) {
                jdbcTemplate.update("ALTER TABLE product ADD COLUMN category_id BIGINT");
                System.out.println("category_id sütunu eklendi.");
            }

            // Kategorileri ekle ve ID'lerini al
            List<Map<String, Object>> categories = jdbcTemplate.queryForList(
                "SELECT DISTINCT category as category_name FROM product WHERE category IS NOT NULL");
            
            for (Map<String, Object> row : categories) {
                String categoryName = (String) row.get("category_name");
                if (categoryName != null && !categoryName.isEmpty()) {
                    // Kategoriyi bul veya oluştur
                    Category category = categoryRepository.findByName(categoryName).orElse(null);
                    
                    if (category == null) {
                        category = new Category();
                        category.setName(categoryName);
                        category.setDescription("Otomatik oluşturuldu");
                        categoryRepository.save(category);
                    }
                    
                    // Kategori ID'si ile ürünleri güncelle - SQL update kullanarak
                    jdbcTemplate.update(
                        "UPDATE product SET category_id = ? WHERE category = ?",
                        category.getId(), categoryName);
                }
            }

            // Otomatik kategori oluşturma kodunu bul ve koşullu hale getir
            // Örneğin aşağıdaki gibi bir if kontrolü ekleyebilirsiniz:
            
            boolean migrationNeeded = false; // Bu değeri uygun bir koşulla belirleyin
            
            if (migrationNeeded) {
                // Sadece gerektiğinde kategori oluştur
                Category newCategory = new Category();
                newCategory.setName("10");
                newCategory.setDescription("Otomatik oluşturuldu");
                categoryRepository.save(newCategory);
            }
            
        } catch (Exception e) {
            System.out.println("Veri geçişi sırasında hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 