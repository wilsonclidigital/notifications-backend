package com.ivansystems.notifications.service;

import com.ivansystems.notifications.dto.MessageRequest;
import com.ivansystems.notifications.exception.NotificationServiceException;
import com.ivansystems.notifications.model.Category;
import com.ivansystems.notifications.model.ChannelType;
import com.ivansystems.notifications.model.NotificationLog;
import com.ivansystems.notifications.model.User;
import com.ivansystems.notifications.repository.NotificationLogRepository;
import com.ivansystems.notifications.service.strategy.NotificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final UserService userService;
    private final NotificationLogRepository logRepository;
    private final List<NotificationStrategy> strategies;

    public NotificationService(UserService userService, NotificationLogRepository logRepository, List<NotificationStrategy> strategies) {
        this.userService = userService;
        this.logRepository = logRepository;
        this.strategies = strategies;
    }

    public void processMessage(MessageRequest request) {
        if (request == null) {
            throw new NotificationServiceException("Message request cannot be null");
        }
        if (request.getCategory() == null) {
            throw new NotificationServiceException("Notification category cannot be null");
        }
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new NotificationServiceException("Notification message cannot be empty");
        }
        log.info("Starting notification process for category: {}", request.getCategory());
        List<User> users = userService.getAllUsers();
        int notifiedUserCount = 0;
        for (User user : users) {
            if (user.getSubscribedCategories().contains(request.getCategory())) {
                notifiedUserCount++;
                notifyUser(user, request.getMessage(), request.getCategory());
            } else {
                log.trace("Skipping user {}: not subscribed to category {}", user.getId(), request.getCategory());
            }
        }
        log.info("Dispatched notification process for category: {}. Targeted {} user(s).", request.getCategory(), notifiedUserCount);
    }

    private void notifyUser(User user, String message, Category category) {
        log.debug("Notifying user {} for category {}", user.getId(), category);
        for (NotificationStrategy strategy : strategies) {
            if (user.getChannels().contains(strategy.getSupportedChannel())) {
                ChannelType channel = strategy.getSupportedChannel();
                log.debug("Attempting to send notification to user {} via {}", user.getId(), channel);
                try {
                    strategy.send(user, message);
                    log.info("Successfully sent {} notification to user {}", channel, user.getId());
                    logNotification(user, channel, message, category);
                } catch (Exception e) {
                    // The existing error log is good, providing essential details for failure analysis.
                    log.error("Failed to send {} notification to user {}", channel, user.getId(), e);
                }
            }
        }
    }

    private void logNotification(User user, ChannelType channel, String message, Category category) {
        NotificationLog notificationLog = new NotificationLog(category, channel, user, message);
        logRepository.save(notificationLog);
    }

    public List<NotificationLog> getLogHistory() {
        return logRepository.findAll();
    }
}