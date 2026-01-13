package com.ivansystems.notifications.service.strategy;

import com.ivansystems.notifications.model.ChannelType;
import com.ivansystems.notifications.model.User;

public interface NotificationStrategy {
    void send(User user, String message);
    ChannelType getSupportedChannel();
}