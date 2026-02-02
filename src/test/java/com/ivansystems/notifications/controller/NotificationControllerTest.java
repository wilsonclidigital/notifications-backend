package com.ivansystems.notifications.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivansystems.notifications.dto.MessageRequest;
import com.ivansystems.notifications.model.Category;
import com.ivansystems.notifications.exception.ChannelNotAvailableException;
import com.ivansystems.notifications.exception.NotificationServiceException;
import com.ivansystems.notifications.model.NotificationLog;
import com.ivansystems.notifications.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendMessage_shouldReturnOk_whenRequestIsValid() throws Exception {
        MessageRequest request = new MessageRequest();
        request.setCategory(Category.MOVIES);
        request.setMessage("New Movie Alert");

        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Message processed successfully"));
    }

    @Test
    void sendMessage_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        MessageRequest request = new MessageRequest();
        // Missing category and message to trigger @Valid failure

        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void sendMessage_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
        MessageRequest request = new MessageRequest();
        request.setCategory(Category.SPORTS);
        request.setMessage("Error trigger");

        doThrow(new NotificationServiceException("Internal error"))
                .when(notificationService).processMessage(any(MessageRequest.class));

        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal error"));
    }

    @Test
    void sendMessage_shouldReturnServiceUnavailable_whenChannelNotAvailable() throws Exception {
        MessageRequest request = new MessageRequest();
        request.setCategory(Category.SPORTS);
        request.setMessage("Channel error");

        doThrow(new ChannelNotAvailableException("SMS gateway down"))
                .when(notificationService).processMessage(any(MessageRequest.class));

        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("SMS gateway down"));
    }

    @Test
    void getLogHistory_shouldReturnLogs() throws Exception {
        // Mock service response
        NotificationLog log = new NotificationLog(); 
        when(notificationService.getLogHistory()).thenReturn(List.of(log));

        mockMvc.perform(get("/api/notifications/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
}