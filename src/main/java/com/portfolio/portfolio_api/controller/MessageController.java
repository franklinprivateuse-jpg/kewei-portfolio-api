package com.portfolio.portfolio_api.controller;

import com.portfolio.portfolio_api.dto.AdminMessageVO;
import com.portfolio.portfolio_api.dto.ChatResponse;
import com.portfolio.portfolio_api.dto.MessageRequest;
import com.portfolio.portfolio_api.dto.MessageVO;
import com.portfolio.portfolio_api.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for chat messages.
 * GET /messages/all is admin-only (handled by AdminTokenFilter).
 *
 * @author Kewei
 * @date 2026-11-11
 */
@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Send a message and get an auto-reply.
     * Returns 201 Created because a new resource (the message pair) is persisted.
     */
    @PostMapping
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        ChatResponse response = messageService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * List chat history for one session (visitor view).
     * Returns 200 with the message list; empty list if the session has no messages.
     */
    @GetMapping
    public ResponseEntity<List<MessageVO>> listChatHistory(@RequestParam String sessionId) {
        return ResponseEntity.ok(messageService.listChatHistory(sessionId));
    }

    /**
     * List all messages across sessions (owner view).
     * Admin-only: AdminTokenFilter rejects requests without a valid X-Admin-Token.
     */
    @GetMapping("/all")
    public ResponseEntity<List<AdminMessageVO>> listAllMessages() {
        return ResponseEntity.ok(messageService.listAllMessages());
    }

    /**
     * Remove chat history for one session.
     * Returns 204 No Content on success — no body needed for a successful deletion.
     */
    @DeleteMapping
    public ResponseEntity<Void> removeChatHistory(@RequestParam String sessionId) {
        messageService.removeChatHistory(sessionId);
        return ResponseEntity.noContent().build();
    }
}