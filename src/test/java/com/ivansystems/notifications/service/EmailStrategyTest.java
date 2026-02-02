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
class EmailStrategyTest {

    @InjectMocks
    private EmailStrategy emailStrategy;

    @Test
    void getSupportedChannel_shouldReturnEmail() {
        assertEquals(ChannelType.EMAIL, emailStrategy.getSupportedChannel());
    }

    @Test
    void send_shouldExecuteSuccessfully() {
        User user = new User("1", "User1", "u1@test.com", "111",
                Set.of(Category.SPORTS), Set.of(ChannelType.EMAIL));

        assertDoesNotThrow(() -> emailStrategy.send(user, "Test Message"));
    }
}