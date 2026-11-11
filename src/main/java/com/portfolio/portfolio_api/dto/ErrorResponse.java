package com.portfolio.portfolio_api.dto;

/**
 * Standard error response body.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public record ErrorResponse(
        int status,
        String error,
        String message
) {
}