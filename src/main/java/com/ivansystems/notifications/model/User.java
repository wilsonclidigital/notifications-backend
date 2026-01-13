package com.ivansystems.notifications.model;

import java.util.Set;

public class User {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private Set<Category> subscribedCategories;
    private Set<ChannelType> channels;

    public User(String id, String name, String email, String phoneNumber, Set<Category> subscribedCategories, Set<ChannelType> channels) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subscribedCategories = subscribedCategories;
        this.channels = channels;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Set<Category> getSubscribedCategories() { return subscribedCategories; }
    public Set<ChannelType> getChannels() { return channels; }
}