package com.portfolio.portfolio_api.dto;

/**
 * Chat message view for visitors (GET /messages?sessionId=...).
 * No sessionId or createdAt exposed.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public record MessageVO(
        Long id,
        String text,
        String sender
) {
}