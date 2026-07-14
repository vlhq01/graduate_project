package com.example.techadvisor.controller;

import com.example.techadvisor.dto.ProductDTO;
import com.example.techadvisor.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping(produces = "application/json")
    public List<ProductDTO> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping(value = "/categories", produces = "application/json")
    public List<String> getAllCategories() {
        return productService.getAllCategories();
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ProductDTO getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @GetMapping(value = "/{id}/similar", produces = "application/json")
    public List<ProductDTO> getSimilarProducts(@PathVariable String id) {
        return productService.getSimilarProducts(id);
    }

    @GetMapping(value = "/homescreen", produces = "application/json")
    public List<ProductDTO> getHomeScreenRecommendationProducts(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = null;
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            userId = (String) auth.getPrincipal();
        }

        // Truyền thêm category xuống Service
        return productService.getHomeRecommendations(userId, category, page, limit);
    }

    @GetMapping(value = "/search", produces = "application/json")
    public List<ProductDTO> searchProducts(@RequestParam("q") String query) {

        // Lấy User ID từ Firebase Token (Đã được Security set vào Context)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (auth != null && auth.getPrincipal() instanceof String)
                ? (String) auth.getPrincipal()
                : "guest_user";
        // Nếu user chưa đăng nhập thì cho ID là guest_user

        return productService.searchProducts(query, userId);
    }
}