package com.example.techadvisor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    private String id;
    private String name;
    private String brand;
    private String category;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> images;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> specs;

    @JdbcTypeCode(SqlTypes.JSON)
    private Price price;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<Review> reviews;

    private Double rating;
    private Integer ratingCount;
    private Integer sold;
    private Integer stock;

    @Data
    public static class Price {
        private Double usd;
        private Double original;
    }

    @Data
    public static class Review {
        private String user;
        private Integer rating;
        private String comment;
        @com.fasterxml.jackson.annotation.JsonProperty("created_at")
        private String createdAt;
    }
}