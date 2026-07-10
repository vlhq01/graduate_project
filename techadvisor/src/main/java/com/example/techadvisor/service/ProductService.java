package com.example.techadvisor.service;

import com.example.techadvisor.client.AiServiceClient;
import com.example.techadvisor.dto.ProductDTO;
import com.example.techadvisor.entity.Product;
import com.example.techadvisor.entity.SearchHistory;
import com.example.techadvisor.mapper.ProductMapper;
import com.example.techadvisor.repository.ProductRepository;
import com.example.techadvisor.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper; // Tiêm Mapper vào
    @Autowired private AiServiceClient aiServiceClient;
    @Autowired private SearchHistoryRepository searchHistoryRepository;

    public List<String> getAllCategories() {
        return productRepository.findAllDistinctCategories();
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto) // Rất xịn và ngắn gọn
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(String id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElse(null);
    }

    public List<ProductDTO> searchProducts(String query, String userId) {

        // 1. LƯU LỊCH SỬ TÌM KIẾM
        if (userId != null && !query.trim().isEmpty()) {
            SearchHistory history = new SearchHistory();
            history.setUserId(userId);
            history.setQuery(query.trim());
            searchHistoryRepository.save(history);
        }

        // 2. GỌI PYTHON AI (HYBRID SEARCH)
        Map<String, Object> req = Map.of(
                "query", query,
                "limit", 10 // Lấy 10 kết quả tốt nhất
        );
        List<String> aiSortedIds = aiServiceClient.searchHybrid(req);

        // 3. KIỂM TRA RỖNG
        if (aiSortedIds == null || aiSortedIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. LẤY TỪ DB VÀ GIỮ NGUYÊN THỨ TỰ CỦA AI (Sorting Magic)
        List<Product> unsortedProducts = productRepository.findAllById(aiSortedIds);

        // Biến List thành Map { "phone_0" -> ProductObj, "phone_1" -> ProductObj }
        Map<String, Product> productMap = unsortedProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // Map ngược lại theo mảng ID của AI
        return aiSortedIds.stream()
                .map(productMap::get)          // Lấy Product từ Map
                .filter(Objects::nonNull)      // Bỏ qua nếu lỡ rớt ID nào đó
                .map(productMapper::toDto)     // Biến Entity -> DTO bằng MapStruct
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getHomeRecommendations(String userId, String category) {

        // ==========================================================
        // TRƯỜNG HỢP 1: USER BẤM VÀO TAB CỤ THỂ (VD: GPU, CPU, Phone)
        // ==========================================================
        if (category != null && !category.equalsIgnoreCase("All") && !category.trim().isEmpty()) {
            // Bỏ qua AI. Chọc thẳng vào DB lấy 10 món bán chạy nhất của danh mục này!
            List<Product> categoryProducts = productRepository.findTop30ByCategoryIgnoreCaseOrderBySoldDesc(category);
            System.out.println("✅✅✅ CHECKPOINT categoriy tab"+ category+" category product" + categoryProducts.toString());
            return categoryProducts.stream().map(productMapper::toDto).collect(Collectors.toList());
        }

        // ==========================================================
        // TRƯỜNG HỢP 2: USER ĐANG Ở TAB "ALL" (Trang chủ tổng hợp)
        // ==========================================================

        // 1. Khách vãng lai (Chưa đăng nhập) -> Lấy Top bán chạy toàn cửa hàng
        if (userId == null || userId.trim().isEmpty()) {
            return getTrendingAll();
        }

        // 2. Khách đã đăng nhập -> Kiểm tra lịch sử tìm kiếm
        List<SearchHistory> histories = searchHistoryRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);

        // 3. Khách chưa tìm kiếm gì bao giờ -> Lấy Top bán chạy toàn cửa hàng
        if (histories == null || histories.isEmpty()) {
            return getTrendingAll();
        }

        // 4. Khách có lịch sử -> Gọi AI mix đồ để gợi ý (Dùng chung hàm Python)
        String aiQuery = histories.stream()
                .map(SearchHistory::getQuery)
                .collect(Collectors.joining(" "));

        Map<String, Object> req = Map.of("query", aiQuery, "limit", 40);
        List<String> aiSortedIds = aiServiceClient.searchHybrid(req); // Gọi sang Python

        return fetchAndSortProducts(aiSortedIds);
    }

    // Hàm phụ trợ cho code gọn hơn
    private List<ProductDTO> getTrendingAll() {
        return productRepository.findTop30ByOrderBySoldDesc().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
    // Hàm phụ nhỏ gọn để lấy Trending
    private List<ProductDTO> getTrendingProducts(String category) {
        List<Product> products = (category == null)
                ? productRepository.findTop30ByOrderBySoldDesc()
                : productRepository.findTop30ByCategoryIgnoreCaseOrderBySoldDesc(category);

        return products.stream().map(productMapper::toDto).collect(Collectors.toList());
    }

    // 2. TÍNH NĂNG SẢN PHẨM TƯƠNG TỰ (DETAIL SCREEN)
    public List<ProductDTO> getSimilarProducts(String productId) {
        List<String> aiSortedIds = aiServiceClient.getSimilarProducts(productId, 4);
        System.out.println("✅✅✅ CHECKPOINT similar product:productid: " + productId+" similar product: " + fetchAndSortProducts(aiSortedIds));

        return fetchAndSortProducts(aiSortedIds);
    }

    private List<ProductDTO> fetchAndSortProducts(List<String> sortedIds) {
        if (sortedIds == null || sortedIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Lấy từ DB lên (Lộn xộn)
        List<Product> unsortedProducts = productRepository.findAllById(sortedIds);

        // Map ID -> Product để truy xuất nhanh
        Map<String, Product> productMap = unsortedProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // Sắp xếp lại chuẩn theo thứ tự AI Python và biến thành DTO
        return sortedIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
}