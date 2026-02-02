package com.ivansystems.notifications.service;

import com.ivansystems.notifications.dto.MessageRequest;
import com.ivansystems.notifications.exception.NotificationServiceException;
import com.ivansystems.notifications.model.*;
import com.ivansystems.notifications.repository.NotificationLogRepository;
import com.ivansystems.notifications.service.strategy.NotificationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void processMessage_shouldContinueWhenOneChannelFails() {
        // Given
        Category category = Category.SPORTS;
        String messageBody = "Sports Update";
        MessageRequest request = new MessageRequest();
        request.setCategory(category);
        request.setMessage(messageBody);

        // User subscribed to SPORTS with EMAIL and SMS
        User user = new User("1", "User1", "u1@test.com", "111",
                Set.of(Category.SPORTS), Set.of(ChannelType.EMAIL, ChannelType.SMS));

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user));

        // Mock Email to fail
        doThrow(new RuntimeException("Email service down")).when(emailStrategy).send(eq(user), any());

        // When
        notificationService.processMessage(request);

        // Then
        // Verify Email was attempted
        verify(emailStrategy).send(eq(user), eq(messageBody));

        // Verify SMS was still called despite Email failure
        verify(smsStrategy).send(eq(user), eq(messageBody));

        // Verify log was saved for the successful SMS notification, but not for the failed email
        verify(logRepository, times(1)).save(any(NotificationLog.class));
    }

    @Test
    void processMessage_shouldHandleUsersWithoutSubscriptions() {
        // Given
        MessageRequest request = new MessageRequest();
        request.setCategory(Category.SPORTS);
        request.setMessage("Update");

        User userNoSub = new User("3", "User3", "u3@test.com", "333",
                Collections.emptySet(), Collections.emptySet());

        when(userService.getAllUsers()).thenReturn(Arrays.asList(userNoSub));

        // When
        notificationService.processMessage(request);

        // Then
        verify(emailStrategy, never()).send(any(), any());
        verify(smsStrategy, never()).send(any(), any());
        verify(logRepository, never()).save(any());
    }

    @Test
    void getLogHistory_shouldReturnAllLogs() {
        // Given
        when(logRepository.findAll()).thenReturn(Arrays.asList(new NotificationLog(), new NotificationLog()));

        // When
        List<NotificationLog> logs = notificationService.getLogHistory();

        // Then
        verify(logRepository, times(1)).findAll();
        org.assertj.core.api.Assertions.assertThat(logs).hasSize(2);
    }

    @Test
    void processMessage_shouldNotNotifyUserWithNoChannels() {
        // Given
        Category category = Category.SPORTS;
        MessageRequest request = new MessageRequest();
        request.setCategory(category);
        request.setMessage("Sports Update");

        // User subscribed to SPORTS but has no notification channels configured
        User userWithNoChannels = new User("4", "User4", "u4@test.com", "444",
                Set.of(Category.SPORTS), Collections.emptySet());

        when(userService.getAllUsers()).thenReturn(List.of(userWithNoChannels));

        // When
        notificationService.processMessage(request);

        // Then
        // But no strategy should be called because the user has no channels
        verify(emailStrategy, never()).send(any(), any());
        verify(smsStrategy, never()).send(any(), any());
        verify(logRepository, never()).save(any());
    }

    @Test
    void processMessage_shouldDoNothingWhenNoUsersExist() {
        // Given
        MessageRequest request = new MessageRequest();
        request.setCategory(Category.SPORTS);
        request.setMessage("Update");

        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // When
        notificationService.processMessage(request);

        // Then
        verify(emailStrategy, never()).send(any(), any());
    }

    @Test
    void processMessage_shouldThrowException_whenRequestIsInvalid() {
        // Test null request
        assertThrows(NotificationServiceException.class, () -> 
            notificationService.processMessage(null));

        // Test null category
        MessageRequest requestNullCategory = new MessageRequest();
        requestNullCategory.setMessage("Msg");
        assertThrows(NotificationServiceException.class, () -> 
            notificationService.processMessage(requestNullCategory));

        // Test empty message
        MessageRequest requestEmptyMsg = new MessageRequest();
        requestEmptyMsg.setCategory(Category.SPORTS);
        requestEmptyMsg.setMessage("");
        assertThrows(NotificationServiceException.class, () -> 
            notificationService.processMessage(requestEmptyMsg));
    }

    @Test
    void processMessage_shouldSkipChannelsWithoutStrategy() {
        // Given service configured ONLY with Email strategy (Simulating SMS strategy missing)
        notificationService = new NotificationService(userService, logRepository, List.of(emailStrategy));

        MessageRequest request = new MessageRequest();
        request.setCategory(Category.SPORTS);
        request.setMessage("Msg");

        // User has SMS channel preference
        User user = new User("6", "User6", "u6@test.com", "666",
                Set.of(Category.SPORTS), Set.of(ChannelType.SMS));

        when(userService.getAllUsers()).thenReturn(List.of(user));

        // When
        notificationService.processMessage(request);

        // Then
        // Email strategy should not be called because user doesn't have Email channel
        verify(emailStrategy, never()).send(any(), any());
        // No exception should be thrown, and execution should complete gracefully
    }
}