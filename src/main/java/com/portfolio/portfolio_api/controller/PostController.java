package com.portfolio.portfolio_api.controller;

import com.portfolio.portfolio_api.dto.PostDetailVO;
import com.portfolio.portfolio_api.dto.PostRequest;
import com.portfolio.portfolio_api.dto.PostVO;
import com.portfolio.portfolio_api.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for blog posts.
 * POST/PUT/DELETE are admin-only (handled by AdminTokenFilter).
 *
 * @author Kewei
 * @date 2026-11-11
 */
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * List all posts (list view, no content).
     * Returns 200 with the post list; empty list if no posts exist.
     */
    @GetMapping
    public ResponseEntity<List<PostVO>> listPosts() {
        return ResponseEntity.ok(postService.listAllPosts());
    }

    /**
     * Get one post by id (detail view, with full content).
     * Returns 200 with the post; 404 if the id does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDetailVO> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    /**
     * Create a new post.
     * Returns 201 Created with the created post.
     */
    @PostMapping
    public ResponseEntity<PostDetailVO> createPost(@Valid @RequestBody PostRequest request) {
        PostDetailVO created = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing post by id.
     * Returns 200 with the updated post; 404 if the id does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostDetailVO> updatePost(@PathVariable Long id,
                                                   @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    /**
     * Delete a post by id.
     * Returns 204 No Content on success; 404 if the id does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removePost(@PathVariable Long id) {
        postService.removePost(id);
        return ResponseEntity.noContent().build();
    }
}