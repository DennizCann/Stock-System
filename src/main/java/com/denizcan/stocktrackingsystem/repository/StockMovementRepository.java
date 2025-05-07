package com.denizcan.stocktrackingsystem.repository;

import com.denizcan.stocktrackingsystem.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    List<StockMovement> findByProductIdOrderByTimestampDesc(Long productId);

    List<StockMovement> findByMovementTypeOrderByTimestampDesc(String movementType);

}