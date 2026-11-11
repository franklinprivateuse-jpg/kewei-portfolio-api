package com.portfolio.portfolio_api.controller;

import com.portfolio.portfolio_api.dto.AdminMessageVO;
import com.portfolio.portfolio_api.dto.ChatResponse;
import com.portfolio.portfolio_api.dto.MessageVO;
import com.portfolio.portfolio_api.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for {@link MessageController}.
 * {@link MessageService} is mocked; filters are disabled to isolate the controller.
 *
 * @author Kewei
 * @date 2026-11-11
 */
@WebMvcTest(MessageController.class)
// Disable filters to isolate the controller layer (skip AdminTokenFilter/SecurityFilter).
// Admin auth logic is covered by AdminTokenFilter's own tests.
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    void should_return201WithChatResponse_when_sendMessage() throws Exception {
        // Arrange
        ChatResponse response = new ChatResponse(
                new MessageVO(1L, "hello", "user"),
                new MessageVO(2L, "Hello! Thanks for visiting my portfolio. How can I help you?", "bot"));
        when(messageService.sendMessage(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"s1\",\"text\":\"hello\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userMessage.id").value(1))
                .andExpect(jsonPath("$.userMessage.text").value("hello"))
                .andExpect(jsonPath("$.userMessage.sender").value("user"))
                .andExpect(jsonPath("$.botMessage.id").value(2))
                .andExpect(jsonPath("$.botMessage.sender").value("bot"));
    }

    @Test
    void should_return400_when_textIsBlank() throws Exception {
        // Arrange
        String body = "{\"sessionId\":\"s1\",\"text\":\"\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void should_return400_when_sessionIdIsMissing() throws Exception {
        // Arrange
        String body = "{\"text\":\"hello\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void should_returnHistory_when_listChatHistory() throws Exception {
        // Arrange
        when(messageService.listChatHistory("s1"))
                .thenReturn(List.of(new MessageVO(1L, "hi", "user")));

        // Act & Assert
        mockMvc.perform(get("/api/v1/messages").param("sessionId", "s1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("hi"))
                .andExpect(jsonPath("$[0].sender").value("user"));
    }

    @Test
    void should_returnAllMessages_when_listAllMessages() throws Exception {
        // Arrange
        when(messageService.listAllMessages()).thenReturn(List.of(
                new AdminMessageVO(1L, "s1", "hi", "user", LocalDateTime.of(2026, 11, 11, 10, 0))));

        // Act & Assert
        mockMvc.perform(get("/api/v1/messages/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].sessionId").value("s1"))
                .andExpect(jsonPath("$[0].text").value("hi"))
                .andExpect(jsonPath("$[0].sender").value("user"));
    }

    @Test
    void should_returnNoContent_when_removeChatHistory() throws Exception {
        // Arrange
        // (no stubbing needed — void method)

        // Act & Assert
        mockMvc.perform(delete("/api/v1/messages").param("sessionId", "s1"))
                .andExpect(status().isNoContent());
        verify(messageService).removeChatHistory("s1");
    }
}