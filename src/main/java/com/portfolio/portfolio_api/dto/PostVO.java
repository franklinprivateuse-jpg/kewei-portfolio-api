package com.portfolio.portfolio_api.dto;

/**
 * Blog post list view (GET /posts).
 * No content field to keep the list response small.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public record PostVO(
        Long id,
        String date,
        String title,
        String excerpt
) {
}