package com.portfolio.portfolio_api.dto;

/**
 * Blog post detail view (GET /posts/{id}, POST /posts, PUT /posts/{id}).
 * Includes the full content field.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public record PostDetailVO(
        Long id,
        String date,
        String title,
        String excerpt,
        String content
) {
}