package com.portfolio.portfolio_api.service;

import com.portfolio.portfolio_api.dto.PostDetailVO;
import com.portfolio.portfolio_api.dto.PostRequest;
import com.portfolio.portfolio_api.dto.PostVO;

import java.util.List;

/**
 * Business logic for blog posts.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public interface PostService {

    /**
     * Create a new blog post.
     *
     * @param request the post data
     * @return the created post with full content
     */
    PostDetailVO createPost(PostRequest request);

    /**
     * List all posts (list view, no content).
     *
     * @return all posts; empty list if none
     */
    List<PostVO> listAllPosts();

    /**
     * Get one post by id (detail view, with content).
     *
     * @param id the post id
     * @return the post with full content
     * @throws com.portfolio.portfolio_api.exception.NotFoundException if the id does not exist
     */
    PostDetailVO getPost(Long id);

    /**
     * Update a post by id.
     *
     * @param id      the post id
     * @param request the new post data
     * @return the updated post with full content
     * @throws com.portfolio.portfolio_api.exception.NotFoundException if the id does not exist
     */
    PostDetailVO updatePost(Long id, PostRequest request);

    /**
     * Delete a post by id.
     *
     * @param id the post id
     * @throws com.portfolio.portfolio_api.exception.NotFoundException if the id does not exist
     */
    void removePost(Long id);
}