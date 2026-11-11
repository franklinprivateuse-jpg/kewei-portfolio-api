package com.portfolio.portfolio_api.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Maps to the 'blog_posts' table.
 * DB column 'post_date' maps to API field 'date'.
 *
 * @author Kewei
 * @date 2026-11-11
 */
@Data
public class Post {

    private Long id;
    private String postDate;
    private String title;
    private String excerpt;
    private String content;
    private LocalDateTime createdAt;
}