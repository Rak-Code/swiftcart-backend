package com.ecommerce.project.service;

import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.ReminderSchedule;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.repository.ReminderScheduleRepository;
import com.ecommerce.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderSchedulerServiceImpl implements ReminderSchedulerService {

    private final ReminderScheduleRepository reminderScheduleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;

    @Override
    public void scheduleCartReminder(String userId, String productId, int delayMinutes) {
        scheduleReminder(userId, productId, ReminderSchedule.ReminderType.CART, delayMinutes);
    }

    @Override
    public void scheduleWishlistReminder(String userId, String productId, int delayMinutes) {
        scheduleReminder(userId, productId, ReminderSchedule.ReminderType.WISHLIST, delayMinutes);
    }

    private void scheduleReminder(String userId, String productId, 
                                 ReminderSchedule.ReminderType type, int delayMinutes) {
        try {
            // Check if reminder already exists
            var existingReminder = reminderScheduleRepository
                    .findByUserIdAndProductIdAndTypeAndStatus(
                            userId, productId, type, ReminderSchedule.ReminderStatus.PENDING
                    );

            if (existingReminder.isPresent()) {
                log.info("Reminder already scheduled for user: {}, product: {}, type: {}", 
                        userId, productId, type);
                return;
            }

            ReminderSchedule reminder = new ReminderSchedule();
            reminder.setUserId(userId);
            reminder.setProductId(productId);
            reminder.setType(type);
            reminder.setStatus(ReminderSchedule.ReminderStatus.PENDING);
            reminder.setScheduledAt(Instant.now().plus(delayMinutes, ChronoUnit.MINUTES));

            reminderScheduleRepository.save(reminder);
            log.info("Scheduled {} reminder for user: {} in {} minutes", type, userId, delayMinutes);
        } catch (Exception e) {
            log.error("Failed to schedule reminder for user: {}, product: {}", userId, productId, e);
        }
    }

    @Override
    @Transactional
    public void cancelReminder(String userId, String productId, ReminderSchedule.ReminderType type) {
        try {
            reminderScheduleRepository.deleteByUserIdAndProductIdAndType(userId, productId, type);
            log.info("Cancelled {} reminder for user: {}, product: {}", type, userId, productId);
        } catch (Exception e) {
            log.error("Failed to cancel reminder for user: {}, product: {}", userId, productId, e);
        }
    }

    @Override
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void processPendingReminders() {
        try {
            List<ReminderSchedule> pendingReminders = reminderScheduleRepository
                    .findByStatusAndScheduledAtBefore(
                            ReminderSchedule.ReminderStatus.PENDING,
                            Instant.now()
                    );

            log.info("Processing {} pending reminders", pendingReminders.size());

            for (ReminderSchedule reminder : pendingReminders) {
                sendReminder(reminder);
            }
        } catch (Exception e) {
            log.error("Error processing pending reminders", e);
        }
    }

    private void sendReminder(ReminderSchedule reminder) {
        try {
            User user = userRepository.findById(reminder.getUserId()).orElse(null);
            Product product = productRepository.findById(reminder.getProductId()).orElse(null);

            if (user == null || product == null) {
                log.warn("User or Product not found for reminder: {}", reminder.getId());
                reminder.setStatus(ReminderSchedule.ReminderStatus.CANCELLED);
                reminderScheduleRepository.save(reminder);
                return;
            }

            // Send appropriate email based on reminder type
            if (reminder.getType() == ReminderSchedule.ReminderType.CART) {
                emailService.sendCartReminderEmail(user, product);
            } else {
                emailService.sendWishlistReminderEmail(user, product);
            }

            // Update reminder status
            reminder.setStatus(ReminderSchedule.ReminderStatus.SENT);
            reminder.setSentAt(Instant.now());
            reminderScheduleRepository.save(reminder);

            log.info("Sent {} reminder to user: {}", reminder.getType(), user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send reminder: {}", reminder.getId(), e);
        }
    }
}
