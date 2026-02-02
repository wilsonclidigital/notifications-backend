package com.ivansystems.notifications.service.strategy;

import com.ivansystems.notifications.model.Category;
import com.ivansystems.notifications.model.ChannelType;
import com.ivansystems.notifications.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SmsStrategyTest {

    @InjectMocks
    private SmsStrategy smsStrategy;

    @Test
    void getSupportedChannel_shouldReturnSms() {
        assertEquals(ChannelType.SMS, smsStrategy.getSupportedChannel());
    }

    @Test
    void send_shouldExecuteSuccessfully() {
        User user = new User("2", "User2", "u2@test.com", "222",
                Set.of(Category.FINANCE), Set.of(ChannelType.SMS));

        assertDoesNotThrow(() -> smsStrategy.send(user, "Test Message"));
    }
}