package com.ivansystems.notifications.repository;

import com.ivansystems.notifications.model.Category;
import com.ivansystems.notifications.model.ChannelType;
import com.ivansystems.notifications.model.NotificationLog;
import com.ivansystems.notifications.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NotificationLogRepositoryTest {

    @Autowired
    private NotificationLogRepository repository;

    @Test
    void shouldSaveAndRetrieveLogs() {
        User user = new User("1", "Test", "test@test.com", "123", Set.of(), Set.of());
        NotificationLog log = new NotificationLog(Category.SPORTS, ChannelType.EMAIL, user, "Test Message");
        
        repository.save(log);
        
        List<NotificationLog> logs = repository.findAllByOrderByTimestampDesc();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getMessageContent()).isEqualTo("Test Message");
    }
}