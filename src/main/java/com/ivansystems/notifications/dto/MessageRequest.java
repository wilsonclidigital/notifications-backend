package com.ivansystems.notifications.dto;

import com.ivansystems.notifications.model.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MessageRequest {

    @NotNull(message = "Category is required")
    private Category category;

    @NotBlank(message = "Message body cannot be empty")
    private String message;

    // Getters and Setters
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
