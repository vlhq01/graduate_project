package com.example.techadvisor.repository;

import com.example.techadvisor.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findTop30ByOrderBySoldDesc();
    List<Product> findTop30ByCategoryIgnoreCaseOrderBySoldDesc(String category);
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL")
    List<String> findAllDistinctCategories();
}
