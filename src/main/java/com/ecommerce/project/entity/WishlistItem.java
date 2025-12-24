package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "wishlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItem {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String productId;

    private LocalDateTime addedAt = LocalDateTime.now();
}
