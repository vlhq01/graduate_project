package com.example.techadvisor.repository;

import com.example.techadvisor.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    // Để dành hàm này cho màn hình Home (Lấy 5 lịch sử gần nhất của 1 User)
    List<SearchHistory> findTop5ByUserIdOrderByCreatedAtDesc(String userId);
}