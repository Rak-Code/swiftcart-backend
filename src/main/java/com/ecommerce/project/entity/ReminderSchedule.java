package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "reminder_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderSchedule {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String productId;

    private ReminderType type; // CART or WISHLIST

    private ReminderStatus status = ReminderStatus.PENDING;

    private Instant scheduledAt;

    private Instant sentAt;

    private Instant createdAt = Instant.now();

    public enum ReminderType {
        CART,
        WISHLIST
    }

    public enum ReminderStatus {
        PENDING,
        SENT,
        CANCELLED
    }
}
