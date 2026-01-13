package com.ivansystems.notifications.service;

import org.springframework.stereotype.Service;

import com.ivansystems.notifications.dto.MessageRequest;
import com.ivansystems.notifications.model.ChannelType;
import com.ivansystems.notifications.model.NotificationLog;
import com.ivansystems.notifications.model.User;
import com.ivansystems.notifications.repository.NotificationLogRepository;
import com.ivansystems.notifications.service.strategy.NotificationStrategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final UserService userService;
    private final NotificationLogRepository logRepository;
    private final Map<ChannelType, NotificationStrategy> strategyMap;

    public NotificationService(UserService userService,
                               NotificationLogRepository logRepository,
                               List<NotificationStrategy> strategies) {
        this.userService = userService;
        this.logRepository = logRepository;

        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(NotificationStrategy::getSupportedChannel, Function.identity()));
    }

    public void processMessage(MessageRequest request) {
        List<User> users = userService.getAllUsers();

        for (User user : users) {
            if (user.getSubscribedCategories().contains(request.getCategory())) {
                notifyUser(user, request);
            }
        }
    }

    private void notifyUser(User user, MessageRequest request) {
        for (ChannelType channel : user.getChannels()) {
            NotificationStrategy strategy = strategyMap.get(channel);
            if (strategy != null) {
                strategy.send(user, request.getMessage());
                logRepository.save(new NotificationLog(request.getCategory(), channel, user, request.getMessage()));
            }
        }
    }

    public List<NotificationLog> getLogHistory() {
        return logRepository.findAllByOrderByTimestampDesc();
    }
}
