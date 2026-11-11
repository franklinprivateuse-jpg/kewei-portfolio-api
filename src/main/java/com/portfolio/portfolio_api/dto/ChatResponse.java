package com.portfolio.portfolio_api.dto;

/**
 * Response for POST /messages: the saved user message and the auto-reply.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public record ChatResponse(
        MessageVO userMessage,
        MessageVO botMessage
) {
}