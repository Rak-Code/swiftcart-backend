package com.ecommerce.project.repository;

import com.ecommerce.project.entity.ReminderSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderScheduleRepository extends MongoRepository<ReminderSchedule, String> {

    List<ReminderSchedule> findByStatusAndScheduledAtBefore(
            ReminderSchedule.ReminderStatus status,
            Instant scheduledAt
    );

    Optional<ReminderSchedule> findByUserIdAndProductIdAndTypeAndStatus(
            String userId,
            String productId,
            ReminderSchedule.ReminderType type,
            ReminderSchedule.ReminderStatus status
    );

    List<ReminderSchedule> findByUserIdAndStatus(
            String userId,
            ReminderSchedule.ReminderStatus status
    );

    void deleteByUserIdAndProductIdAndType(
            String userId,
            String productId,
            ReminderSchedule.ReminderType type
    );
}
