package com.portfolio.portfolio_api.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Maps to the 'messages' table.
 * DB column 'content' maps to API field 'text'.
 *
 * @author Kewei
 * @date 2026-11-11
 */
@Data
public class Message {

    private Long id;
    private String sessionId;
    private String sender;
    private String content;
    private LocalDateTime createdAt;
}