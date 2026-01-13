package com.ivansystems.notifications.service.strategy;

import org.springframework.stereotype.Component;

import com.ivansystems.notifications.model.ChannelType;
import com.ivansystems.notifications.model.User;

@Component
public class EmailStrategy implements NotificationStrategy {
    @Override
    public void send(User user, String message) {
        System.out.println("[Email] Sending to " + user.getEmail() + ": " + message);
    }

    @Override
    public ChannelType getSupportedChannel() {
        return ChannelType.EMAIL;
    }
}
