package com.example.techadvisor.service;

import com.example.techadvisor.client.AiServiceClient;
import com.example.techadvisor.dto.ProductDTO;
import com.example.techadvisor.entity.Product;
import com.example.techadvisor.entity.SearchHistory;
import com.example.techadvisor.mapper.ProductMapper;
import com.example.techadvisor.repository.ProductRepository;
import com.example.techadvisor.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private AiServiceClient aiServiceClient;
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    public List<String> getAllCategories() {
        return productRepository.findAllDistinctCategories();
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(String id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElse(null);
    }

    public List<ProductDTO> searchProducts(String query, String userId) {

        if (userId != null && !query.trim().isEmpty()) {
            SearchHistory history = new SearchHistory();
            history.setUserId(userId);
            history.setQuery(query.trim());
            searchHistoryRepository.save(history);
        }

        Map<String, Object> req = Map.of(
                "query", query,
                "limit", 10
        );
        List<String> aiSortedIds = aiServiceClient.searchHybrid(req);

        if (aiSortedIds == null || aiSortedIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> unsortedProducts = productRepository.findAllById(aiSortedIds);

        Map<String, Product> productMap = unsortedProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        return aiSortedIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getHomeRecommendations(String userId, String category, int page, int limit) {

        int pageIndex = Math.max(page - 1, 0);


        if (category != null && !category.equalsIgnoreCase("All") && !category.trim().isEmpty()) {
            Pageable pageable = PageRequest.of(pageIndex, limit);

            Page<Product> categoryProducts = productRepository.findByCategoryIgnoreCaseOrderBySoldDescIdAsc(category, pageable);

            System.out.println("✅[CATEGORY] Tab: " + category + " | Page: " + page + " | Limit: " + limit);
            return categoryProducts.getContent().stream()
                    .map(productMapper::toDto)
                    .collect(Collectors.toList());
        }

        List<ProductDTO> basePool;

        boolean isGuestOrNoHistory = true;
        List<SearchHistory> histories = null;

        if (userId != null && !userId.trim().isEmpty()) {
            histories = searchHistoryRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
            if (histories != null && !histories.isEmpty()) {
                isGuestOrNoHistory = false;
            }
        }

        if (isGuestOrNoHistory) {
            Pageable top40Pageable = PageRequest.of(
                    0,
                    40,
                    Sort.by(Sort.Direction.DESC, "sold").and(Sort.by(Sort.Direction.ASC, "id"))
            );
            basePool = productRepository.findAll(top40Pageable)
                    .getContent().stream()
                    .map(productMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            String aiQuery = histories.stream()
                    .map(SearchHistory::getQuery)
                    .collect(Collectors.joining(" "));

            Map<String, Object> req = Map.of("query", aiQuery, "limit", 40);
            List<String> aiSortedIds = aiServiceClient.searchHybrid(req);

            basePool = fetchAndSortProducts(aiSortedIds);
        }

        int fromIndex = pageIndex * limit;

        if (fromIndex < basePool.size()) {
            int toIndex = Math.min(fromIndex + limit, basePool.size());

            System.out.println("🤖[AI/BASE SUGGESTION] Đang hiển thị sản phẩm ưu tiên. Page: " + page);
            return basePool.subList(fromIndex, toIndex);
        } else {
            List<String> excludeIds = basePool.stream()
                    .map(ProductDTO::getId)
                    .collect(Collectors.toList());

            if (excludeIds.isEmpty()) {
                excludeIds = List.of("DUMMY_ID_FOR_SQL_EXCLUSION");
            }

            int trendingPage = pageIndex - (basePool.size() / limit);
            Pageable pageable = PageRequest.of(trendingPage, limit);

            Page<Product> trendingProducts = productRepository.findByIdNotInOrderBySoldDescIdAsc(excludeIds, pageable);

            System.out.println("🔥[INFINITE SCROLL] Đã chuyển sang load Trending (Lọc trùng thành công) tại trang: " + trendingPage);
            return trendingProducts.getContent().stream()
                    .map(productMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    public List<ProductDTO> getSimilarProducts(String productId) {
        List<String> aiSortedIds = aiServiceClient.getSimilarProducts(productId, 4);
        System.out.println("✅✅✅ CHECKPOINT similar product:productid: " + productId + " similar product: " + fetchAndSortProducts(aiSortedIds));

        return fetchAndSortProducts(aiSortedIds);
    }

    private List<ProductDTO> fetchAndSortProducts(List<String> sortedIds) {
        if (sortedIds == null || sortedIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> unsortedProducts = productRepository.findAllById(sortedIds);

        Map<String, Product> productMap = unsortedProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        return sortedIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
}