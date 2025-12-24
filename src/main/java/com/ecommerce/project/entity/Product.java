package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    @Indexed
    private String categoryId;

    private double price;

    private int stockQuantity;

    private List<String> imageUrls;

    private Size size;

    private String color;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Size { XS, S, M, L, XL, XXL, XXXL }
}
