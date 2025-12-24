package com.ecommerce.project.service;

import com.ecommerce.project.entity.ReminderSchedule;

public interface ReminderSchedulerService {

    void scheduleCartReminder(String userId, String productId, int delayMinutes);

    void scheduleWishlistReminder(String userId, String productId, int delayMinutes);

    void cancelReminder(String userId, String productId, ReminderSchedule.ReminderType type);

    void processPendingReminders();
}
