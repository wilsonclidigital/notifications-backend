package com.ivansystems.notifications.service;

import org.springframework.stereotype.Service;

import com.ivansystems.notifications.model.Category;
import com.ivansystems.notifications.model.ChannelType;
import com.ivansystems.notifications.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    public List<User> getAllUsers() {
        return Arrays.asList(
            new User("1", "Juan Perez", "jperez@example.com", "1234567890",
                Set.of(Category.SPORTS, Category.MOVIES),
                Set.of(ChannelType.SMS, ChannelType.EMAIL)),
            new User("2", "Maria Gomez", "mgomez@example.com", "0987654321",
                Set.of(Category.FINANCE),
                Set.of(ChannelType.PUSH_NOTIFICATION)),
            new User("3", "Julio Suarez", "jsuarez@example.com", "1122334455",
                Set.of(Category.SPORTS, Category.FINANCE, Category.MOVIES),
                Set.of(ChannelType.EMAIL))
        );
    }
}