package com.portfolio.portfolio_api.service.impl;

import com.portfolio.portfolio_api.dto.PostDetailVO;
import com.portfolio.portfolio_api.dto.PostRequest;
import com.portfolio.portfolio_api.dto.PostVO;
import com.portfolio.portfolio_api.entity.Post;
import com.portfolio.portfolio_api.exception.NotFoundException;
import com.portfolio.portfolio_api.mapper.PostMapper;
import com.portfolio.portfolio_api.service.PostService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of {@link PostService}.
 *
 * @author Kewei
 * @date 2026-11-11
 */
@Service
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    public PostServiceImpl(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    public PostDetailVO createPost(PostRequest request) {
        Post post = toEntity(request);
        postMapper.insert(post);
        return toDetailVO(post);
    }

    @Override
    public List<PostVO> listAllPosts() {
        return postMapper.listAll().stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public PostDetailVO getPost(Long id) {
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new NotFoundException("Post not found");
        }
        return toDetailVO(post);
    }

    @Override
    public PostDetailVO updatePost(Long id, PostRequest request) {
        // Check existence first so a missing id returns 404, not a silent no-op
        Post existing = postMapper.findById(id);
        if (existing == null) {
            throw new NotFoundException("Post not found");
        }
        Post post = toEntity(request);
        post.setId(id);
        postMapper.updateById(post);
        return toDetailVO(post);
    }

    @Override
    public void removePost(Long id) {
        int deleted = postMapper.deleteById(id);
        if (deleted == 0) {
            throw new NotFoundException("Post not found");
        }
    }

    /**
     * Convert request to entity (id not set; caller sets it for updates).
     *
     * @param request the post request
     * @return the entity without id
     */
    private Post toEntity(PostRequest request) {
        Post post = new Post();
        post.setPostDate(request.date());
        post.setTitle(request.title());
        post.setExcerpt(request.excerpt());
        post.setContent(request.content());
        return post;
    }

    /**
     * Convert entity to list view (no content).
     *
     * @param post the entity
     * @return list-visible VO
     */
    private PostVO toVO(Post post) {
        return new PostVO(post.getId(), post.getPostDate(), post.getTitle(), post.getExcerpt());
    }

    /**
     * Convert entity to detail view (with content).
     *
     * @param post the entity
     * @return detail VO with full content
     */
    private PostDetailVO toDetailVO(Post post) {
        return new PostDetailVO(post.getId(), post.getPostDate(), post.getTitle(),
                post.getExcerpt(), post.getContent());
    }
}