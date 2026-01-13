package com.ivansystems.notifications.service.strategy;

import org.springframework.stereotype.Component;

import com.ivansystems.notifications.model.ChannelType;
import com.ivansystems.notifications.model.User;

@Component
public class PushNotificationStrategy implements NotificationStrategy {
    @Override
    public void send(User user, String message) {
        System.out.println("[Push] Sending to user " + user.getId() + ": " + message);
    }

    @Override
    public ChannelType getSupportedChannel() {
        return ChannelType.PUSH_NOTIFICATION;
    }
}
