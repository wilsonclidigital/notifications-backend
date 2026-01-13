package com.ivansystems.notifications.service;

import com.ivansystems.notifications.dto.MessageRequest;
import com.ivansystems.notifications.model.*;
import com.ivansystems.notifications.repository.NotificationLogRepository;
import com.ivansystems.notifications.service.strategy.NotificationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private NotificationLogRepository logRepository;

    @Mock
    private NotificationStrategy emailStrategy;

    @Mock
    private NotificationStrategy smsStrategy;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        // Setup mock strategies behavior
        lenient().when(emailStrategy.getSupportedChannel()).thenReturn(ChannelType.EMAIL);
        lenient().when(smsStrategy.getSupportedChannel()).thenReturn(ChannelType.SMS);

        List<NotificationStrategy> strategies = Arrays.asList(emailStrategy, smsStrategy);
        notificationService = new NotificationService(userService, logRepository, strategies);
    }

    @Test
    void processMessage_shouldNotifyUsersSubscribedToCategory() {
        // Given
        Category category = Category.SPORTS;
        String messageBody = "Sports Update";
        MessageRequest request = new MessageRequest();
        request.setCategory(category);
        request.setMessage(messageBody);

        // User 1: Subscribed to SPORTS, uses EMAIL
        User user1 = new User("1", "User1", "u1@test.com", "111",
                Set.of(Category.SPORTS), Set.of(ChannelType.EMAIL));
        
        // User 2: Subscribed to FINANCE (not SPORTS), uses SMS
        User user2 = new User("2", "User2", "u2@test.com", "222",
                Set.of(Category.FINANCE), Set.of(ChannelType.SMS));

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // When
        notificationService.processMessage(request);

        // Then
        // Verify emailStrategy was called for user1
        verify(emailStrategy, times(1)).send(eq(user1), eq(messageBody));
        
        // Verify smsStrategy was NOT called (user2 not subscribed to SPORTS)
        verify(smsStrategy, never()).send(any(), any());

        // Verify log was saved for the successful notification
        verify(logRepository, times(1)).save(any(NotificationLog.class));
    }
}