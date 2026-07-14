package com.example.techadvisor.repository;

import com.example.techadvisor.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findByCategoryIgnoreCaseOrderBySoldDescIdAsc(String category, Pageable pageable);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL")
    List<String> findAllDistinctCategories();

    Page<Product> findByIdNotInOrderBySoldDescIdAsc(List<String> excludedIds, Pageable pageable);
}
