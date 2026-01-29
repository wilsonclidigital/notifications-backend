package com.ivansystems.notifications.service;

import com.ivansystems.notifications.dto.MessageRequest;
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
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if (user.getSubscribedCategories().contains(request.getCategory())) {
                notifyUser(user, request.getMessage());
            }
        }
    }

    private void notifyUser(User user, String message) {
        for (NotificationStrategy strategy : strategies) {
            if (user.getChannels().contains(strategy.getSupportedChannel())) {
                try {
                    strategy.send(user, message);
                    logNotification(user, strategy.getSupportedChannel().name(), message);
                } catch (Exception e) {
                    log.error("Failed to send {} notification to user {}", strategy.getSupportedChannel(), user.getId(), e);
                }
            }
        }
    }

    private void logNotification(User user, String channel, String message) {
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setUserId(user.getId());
        notificationLog.setChannel(channel);
        notificationLog.setMessage(message);
        notificationLog.setTimestamp(LocalDateTime.now());
        logRepository.save(notificationLog);
    }
}