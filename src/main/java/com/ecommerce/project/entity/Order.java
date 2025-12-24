package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String id;

    @Indexed
    private String userId;

    private Address address; // Embedded address snapshot

    private double totalAmount;

    private Status status = Status.pending;

    private LocalDateTime orderDate = LocalDateTime.now();

    private List<OrderItem> items;

    public enum Status {
        pending, processing, shipped, delivered, cancelled
    }

    // Embedded Address Model (snapshot of address at order time)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {

        private String addressId;

        private String addressLine;

        private String city;

        private String state;

        private String postalCode;

        private String country;
    }

    // Embedded OrderItem
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {

        private String productId;

        private int quantity;

        private double price;
    }
}
