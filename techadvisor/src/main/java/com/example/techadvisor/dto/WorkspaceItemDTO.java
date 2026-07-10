package com.example.techadvisor.dto;

import com.example.techadvisor.entity.Product;
import lombok.Data;

@Data
public class WorkspaceItemDTO {
    private String productId;
    private String name;
    private String brand;

    private Product.Price price; // Sửa priceUsd thành đối tượng Price

    private String thumbnailUrl;
    private Double rating;
}
