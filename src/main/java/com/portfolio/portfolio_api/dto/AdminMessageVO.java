package com.portfolio.portfolio_api.dto;

import java.time.LocalDateTime;

/**
 * Full message view for the owner (GET /messages/all).
 * Includes sessionId and createdAt.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public record AdminMessageVO(
        Long id,
        String sessionId,
        String text,
        String sender,
        LocalDateTime createdAt
) {
}