package com.portfolio.portfolio_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request body for POST /posts and PUT /posts/{id}.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public record PostRequest(

        /** Publish date in yyyy-MM-dd format. */
        @NotBlank(message = "date must not be empty")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "date must be in yyyy-MM-dd format")
        String date,

        /** Post title. */
        @NotBlank(message = "title must not be empty")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        /** Short summary; optional. */
        @Size(max = 500, message = "excerpt must be at most 500 characters")
        String excerpt,

        /** Full article content. */
        @NotBlank(message = "content must not be empty")
        String content
) {
}