package com.example.techadvisor.repository;

import com.example.techadvisor.entity.WorkspaceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceItemRepository extends JpaRepository<WorkspaceItem, Long> {

    List<WorkspaceItem> findByUserId(String userId);

    void deleteByUserIdAndProductId(String userId, String productId);

    boolean existsByUserIdAndProductId(String userId, String productId);
}