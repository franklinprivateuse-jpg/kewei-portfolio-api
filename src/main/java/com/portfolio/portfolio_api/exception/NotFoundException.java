package com.portfolio.portfolio_api.exception;

/**
 * Thrown when a requested resource does not exist (e.g. post id not found).
 * Handled by {@link GlobalExceptionHandler} and returned as HTTP 404.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}