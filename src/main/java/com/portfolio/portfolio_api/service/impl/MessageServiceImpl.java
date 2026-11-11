package com.portfolio.portfolio_api.service.impl;

import com.portfolio.portfolio_api.dto.AdminMessageVO;
import com.portfolio.portfolio_api.dto.ChatResponse;
import com.portfolio.portfolio_api.dto.MessageRequest;
import com.portfolio.portfolio_api.dto.MessageVO;
import com.portfolio.portfolio_api.entity.Message;
import com.portfolio.portfolio_api.mapper.MessageMapper;
import com.portfolio.portfolio_api.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link MessageService}.
 * Auto-reply rules migrated from frontend useChat.ts REPLY_RULES.
 *
 * @author Kewei
 * @date 2026-11-11
 */
@Service
public class MessageServiceImpl implements MessageService {

    /** Sender value for messages written by the visitor. */
    private static final String SENDER_USER = "user";

    /** Sender value for auto-generated replies. */
    private static final String SENDER_BOT = "bot";

    /** Default reply when no keyword matches. */
    private static final String DEFAULT_REPLY =
            "Thanks for your message! I will get back to you soon. You can also explore the About, Knowledge and Blog pages.";

    /** Keyword-to-reply rules for generating relevant auto-replies. */
    private static final List<ReplyRule> REPLY_RULES = List.of(
            new ReplyRule(List.of("hello", "hi", "hey", "greetings"),
                    "Hello! Thanks for visiting my portfolio. How can I help you?"),
            new ReplyRule(List.of("job", "work", "experience", "hire", "career"),
                    "I have seven years of industry experience — check out the About and Education pages for details!"),
            new ReplyRule(List.of("project", "portfolio", "website", "app"),
                    "This portfolio is built with React, TypeScript and Vite. The Gallery page has project highlights."),
            new ReplyRule(List.of("skill", "tech", "technology", "stack", "language"),
                    "My main skills include Java, TypeScript and React — see the Knowledge page for the full list."),
            new ReplyRule(List.of("education", "study", "university", "degree", "master"),
                    "I am studying for an MSc in Software Engineering at the University of Limerick."),
            new ReplyRule(List.of("contact", "email", "reach", "message"),
                    "You can leave a message here and I will get back to you. Thanks for reaching out!"),
            new ReplyRule(List.of("blog", "article", "post", "write"),
                    "I write about software development on the Blog page — take a look!")
    );

    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    // Transactional: both inserts must succeed or fail together,
    // so we never save a user message without its bot reply.
    @Transactional
    public ChatResponse sendMessage(MessageRequest request) {
        // 1. Save the visitor's original message
        Message userMessage = new Message();
        userMessage.setSessionId(request.sessionId());
        userMessage.setSender(SENDER_USER);
        userMessage.setContent(request.text());
        messageMapper.insert(userMessage);

        // 2. Generate an auto-reply by keyword matching and save it
        Message botMessage = new Message();
        botMessage.setSessionId(request.sessionId());
        botMessage.setSender(SENDER_BOT);
        botMessage.setContent(pickReply(request.text()));
        messageMapper.insert(botMessage);

        // 3. Return visitor views of both messages (no admin fields)
        return new ChatResponse(toVO(userMessage), toVO(botMessage));
    }

    @Override
    public List<MessageVO> listChatHistory(String sessionId) {
        return messageMapper.listBySessionId(sessionId).stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public List<AdminMessageVO> listAllMessages() {
        return messageMapper.listAll().stream()
                .map(this::toAdminVO)
                .toList();
    }

    @Override
    public void removeChatHistory(String sessionId) {
        messageMapper.deleteBySessionId(sessionId);
    }

    /**
     * Pick a relevant reply based on keywords in the user's message.
     *
     * @param userText the raw user message
     * @return the matched reply, or {@link #DEFAULT_REPLY} if none matches
     */
    private String pickReply(String userText) {
        // Lowercase for case-insensitive match; first matched rule wins
        String lower = userText.toLowerCase();
        for (ReplyRule rule : REPLY_RULES) {
            for (String keyword : rule.keywords()) {
                if (lower.contains(keyword)) {
                    return rule.reply();
                }
            }
        }
        // No rule matched: return the fallback reply
        return DEFAULT_REPLY;
    }

    /**
     * Convert entity to visitor view.
     *
     * @param message the entity
     * @return visitor-visible VO
     */
    private MessageVO toVO(Message message) {
        return new MessageVO(message.getId(), message.getContent(), message.getSender());
    }

    /**
     * Convert entity to owner view.
     *
     * @param message the entity
     * @return owner-visible VO with sessionId and createdAt
     */
    private AdminMessageVO toAdminVO(Message message) {
        return new AdminMessageVO(message.getId(), message.getSessionId(),
                message.getContent(), message.getSender(), message.getCreatedAt());
    }

    /** A keyword-to-reply rule. */
    private record ReplyRule(List<String> keywords, String reply) {
    }
}