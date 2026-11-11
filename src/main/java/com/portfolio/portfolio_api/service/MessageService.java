package com.portfolio.portfolio_api.service;

import com.portfolio.portfolio_api.dto.AdminMessageVO;
import com.portfolio.portfolio_api.dto.ChatResponse;
import com.portfolio.portfolio_api.dto.MessageRequest;
import com.portfolio.portfolio_api.dto.MessageVO;

import java.util.List;

/**
 * Business logic for chat messages.
 * Auto-reply rules migrated from frontend useChat.ts REPLY_RULES.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public interface MessageService {

    /**
     * Save user message, generate and save auto-reply, return both.
     *
     * @param request the user message request
     * @return the saved user message and the auto-reply
     */
    ChatResponse sendMessage(MessageRequest request);

    /**
     * List chat history for one session (visitor view).
     *
     * @param sessionId the chat session id
     * @return visitor-visible messages; empty list if none
     */
    List<MessageVO> listChatHistory(String sessionId);

    /**
     * List all messages (owner view).
     *
     * @return all messages with full fields; empty list if none
     */
    List<AdminMessageVO> listAllMessages();

    /**
     * Remove chat history for one session.
     *
     * @param sessionId the chat session id
     */
    void removeChatHistory(String sessionId);
}