package com.example.techadvisor.dto;

import com.example.techadvisor.entity.Product;
import com.example.techadvisor.entity.Product.Review; // Import cái lớp nội ở trên
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private String brand;
    private String category;
    private List<String> images;
    private Map<String, String> specs;
    private Product.Price price;    private Double rating;
    private Integer ratingCount;
    private List<Review> reviews; // Thêm reviews
}