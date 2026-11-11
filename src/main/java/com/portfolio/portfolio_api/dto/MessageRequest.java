package com.portfolio.portfolio_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for POST /messages.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public record MessageRequest(

        /** Unique chat session identifier; groups messages into conversation threads. */
        @NotBlank(message = "sessionId must not be empty")
        @Size(max = 64, message = "sessionId must be at most 64 characters")
        String sessionId,

        /** Message text typed by the visitor; matched against reply keywords. */
        @NotBlank(message = "text must not be empty")
        @Size(max = 1000, message = "text must be at most 1000 characters")
        String text
) {
}