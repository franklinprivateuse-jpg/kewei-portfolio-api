package com.portfolio.portfolio_api.mapper;

import com.portfolio.portfolio_api.entity.Post;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Persistence-layer tests for {@link PostMapper} against an in-memory H2 database.
 * Each test runs in a rolled-back transaction (test isolation guaranteed by {@code @MybatisTest}).
 *
 * @author Kewei
 * @date 2026-11-11
 */
@MybatisTest
class PostMapperTest {

    @Autowired
    private PostMapper postMapper;

    @Test
    void should_setGeneratedId_when_insert() {
        // Arrange
        Post post = buildPost("2026-11-11", "Title", "Excerpt", "Content");

        // Act
        postMapper.insert(post);

        // Assert
        assertThat(post.getId()).isNotNull().isPositive();
    }

    @Test
    void should_persistAllFields_when_findById() {
        // Arrange
        Post post = buildPost("2026-11-11", "My Title", "Short summary", "Full article body");
        postMapper.insert(post);

        // Act
        Post found = postMapper.findById(post.getId());

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getPostDate()).isEqualTo("2026-11-11");
        assertThat(found.getTitle()).isEqualTo("My Title");
        assertThat(found.getExcerpt()).isEqualTo("Short summary");
        assertThat(found.getContent()).isEqualTo("Full article body");
        assertThat(found.getCreatedAt()).isNotNull();
    }

    @Test
    void should_returnNull_when_findByIdNotExists() {
        // Arrange
        // (no data inserted)

        // Act
        Post found = postMapper.findById(999L);

        // Assert
        assertThat(found).isNull();
    }

    @Test
    void should_notIncludeContent_when_listAll() {
        // Arrange
        postMapper.insert(buildPost("2026-11-11", "Title 1", "Excerpt 1", "Long content 1"));
        postMapper.insert(buildPost("2026-11-12", "Title 2", "Excerpt 2", "Long content 2"));

        // Act
        List<Post> result = postMapper.listAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getContent() == null);
        assertThat(result).allMatch(p -> p.getCreatedAt() == null);
    }

    @Test
    void should_returnNewestFirst_when_listAll() {
        // Arrange
        postMapper.insert(buildPost("2026-11-11", "Old post", "E1", "C1"));
        postMapper.insert(buildPost("2026-11-12", "New post", "E2", "C2"));

        // Act
        List<Post> result = postMapper.listAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("New post");
        assertThat(result.get(1).getTitle()).isEqualTo("Old post");
    }

    @Test
    void should_returnEmptyList_when_noPosts() {
        // Arrange
        // (no data inserted)

        // Act
        List<Post> result = postMapper.listAll();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void should_updateAllFields_when_updateById() {
        // Arrange
        Post post = buildPost("2026-11-11", "Old title", "Old excerpt", "Old content");
        postMapper.insert(post);

        Post update = buildPost("2026-11-12", "New title", "New excerpt", "New content");
        update.setId(post.getId());

        // Act
        int affected = postMapper.updateById(update);

        // Assert
        assertThat(affected).isEqualTo(1);
        Post found = postMapper.findById(post.getId());
        assertThat(found.getPostDate()).isEqualTo("2026-11-12");
        assertThat(found.getTitle()).isEqualTo("New title");
        assertThat(found.getExcerpt()).isEqualTo("New excerpt");
        assertThat(found.getContent()).isEqualTo("New content");
    }

    @Test
    void should_returnZero_when_updateNonexistentId() {
        // Arrange
        Post update = buildPost("2026-11-11", "Title", "Excerpt", "Content");
        update.setId(999L);

        // Act
        int affected = postMapper.updateById(update);

        // Assert
        assertThat(affected).isZero();
    }

    @Test
    void should_deletePost_when_deleteById() {
        // Arrange
        Post post = buildPost("2026-11-11", "Title", "Excerpt", "Content");
        postMapper.insert(post);

        // Act
        int deleted = postMapper.deleteById(post.getId());

        // Assert
        assertThat(deleted).isEqualTo(1);
        assertThat(postMapper.findById(post.getId())).isNull();
    }

    @Test
    void should_returnZero_when_deleteNonexistentId() {
        // Arrange
        // (no data inserted)

        // Act
        int deleted = postMapper.deleteById(999L);

        // Assert
        assertThat(deleted).isZero();
    }

    private Post buildPost(String date, String title, String excerpt, String content) {
        Post post = new Post();
        post.setPostDate(date);
        post.setTitle(title);
        post.setExcerpt(excerpt);
        post.setContent(content);
        return post;
    }
}