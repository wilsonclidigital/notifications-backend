package com.ivansystems.notifications.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private ChannelType channel;

    private String userId;
    private String userName;
    private String messageContent;
    private LocalDateTime timestamp;

    public NotificationLog() {}

    public NotificationLog(Category category, ChannelType channel, User user, String messageContent) {
        this.category = category;
        this.channel = channel;
        this.userId = user.getId();
        this.userName = user.getName();
        this.messageContent = messageContent;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public Category getCategory() { return category; }
    public ChannelType getChannel() { return channel; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getMessageContent() { return messageContent; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
