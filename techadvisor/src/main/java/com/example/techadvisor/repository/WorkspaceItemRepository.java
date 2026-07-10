package com.example.techadvisor.repository;

import com.example.techadvisor.entity.WorkspaceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceItemRepository extends JpaRepository<WorkspaceItem, Long> {

    // 1. Lấy tất cả sản phẩm trong Workspace của 1 User
    List<WorkspaceItem> findByUserId(String userId);

    // 2. Xóa 1 sản phẩm khỏi Workspace
    void deleteByUserIdAndProductId(String userId, String productId);

    // 3. Kiểm tra xem user đã lưu sản phẩm này chưa (để hiển thị nút Trái tim đỏ/trắng trên App)
    boolean existsByUserIdAndProductId(String userId, String productId);
}