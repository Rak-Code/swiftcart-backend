package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String productId;

    private int quantity = 1;

    private LocalDateTime addedAt = LocalDateTime.now();
}
