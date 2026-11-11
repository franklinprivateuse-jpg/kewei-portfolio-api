package com.portfolio.portfolio_api.service;

import com.portfolio.portfolio_api.dto.PostDetailVO;
import com.portfolio.portfolio_api.dto.PostRequest;
import com.portfolio.portfolio_api.dto.PostVO;
import com.portfolio.portfolio_api.entity.Post;
import com.portfolio.portfolio_api.exception.NotFoundException;
import com.portfolio.portfolio_api.mapper.PostMapper;
import com.portfolio.portfolio_api.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PostServiceImpl}.
 * Business logic only; {@link PostMapper} is mocked.
 *
 * @author Kewei
 * @date 2026-11-11
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void should_saveAllFields_when_createPost() {
        // Arrange
        PostRequest request = new PostRequest("2026-11-11", "My Title", "Summary", "Body text");
        // Simulate DB auto-increment: set id=1 on insert
        doAnswer(invocation -> {
            invocation.<Post>getArgument(0).setId(1L);
            return null;
        }).when(postMapper).insert(any(Post.class));

        // Act
        PostDetailVO result = postService.createPost(request);

        // Assert
        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postMapper).insert(captor.capture());
        Post saved = captor.getValue();
        assertThat(saved.getPostDate()).isEqualTo("2026-11-11");
        assertThat(saved.getTitle()).isEqualTo("My Title");
        assertThat(saved.getExcerpt()).isEqualTo("Summary");
        assertThat(saved.getContent()).isEqualTo("Body text");
        assertThat(result).isEqualTo(new PostDetailVO(1L, "2026-11-11", "My Title", "Summary", "Body text"));
    }

    @Test
    void should_returnListWithoutContent_when_listAllPosts() {
        // Arrange
        Post post = buildPost(1L, "2026-11-11", "Title", "Excerpt", "Content");
        when(postMapper.listAll()).thenReturn(List.of(post));

        // Act
        List<PostVO> result = postService.listAllPosts();

        // Assert
        assertThat(result).containsExactly(new PostVO(1L, "2026-11-11", "Title", "Excerpt"));
    }

    @Test
    void should_returnEmptyList_when_noPosts() {
        // Arrange
        when(postMapper.listAll()).thenReturn(List.of());

        // Act
        List<PostVO> result = postService.listAllPosts();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void should_returnDetail_when_getPostExists() {
        // Arrange
        Post post = buildPost(1L, "2026-11-11", "Title", "Excerpt", "Full content");
        when(postMapper.findById(1L)).thenReturn(post);

        // Act
        PostDetailVO result = postService.getPost(1L);

        // Assert
        assertThat(result).isEqualTo(new PostDetailVO(1L, "2026-11-11", "Title", "Excerpt", "Full content"));
    }

    @Test
    void should_throwNotFound_when_getPostNotExists() {
        // Arrange
        when(postMapper.findById(999L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> postService.getPost(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Post not found");
    }

    @Test
    void should_updateAndReturnDetail_when_updatePostExists() {
        // Arrange
        Post existing = buildPost(1L, "2026-11-11", "Old", "Old excerpt", "Old content");
        when(postMapper.findById(1L)).thenReturn(existing);
        PostRequest request = new PostRequest("2026-11-12", "New Title", "New excerpt", "New content");

        // Act
        PostDetailVO result = postService.updatePost(1L, request);

        // Assert
        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postMapper).updateById(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(1L);
        assertThat(captor.getValue().getTitle()).isEqualTo("New Title");
        assertThat(result).isEqualTo(new PostDetailVO(1L, "2026-11-12", "New Title", "New excerpt", "New content"));
    }

    @Test
    void should_throwNotFound_when_updatePostNotExists() {
        // Arrange
        when(postMapper.findById(999L)).thenReturn(null);
        PostRequest request = new PostRequest("2026-11-11", "Title", "Excerpt", "Content");

        // Act & Assert
        assertThatThrownBy(() -> postService.updatePost(999L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Post not found");
    }

    @Test
    void should_deletePost_when_removePostExists() {
        // Arrange
        when(postMapper.deleteById(1L)).thenReturn(1);

        // Act
        postService.removePost(1L);

        // Assert
        verify(postMapper).deleteById(1L);
    }

    @Test
    void should_throwNotFound_when_removePostNotExists() {
        // Arrange
        when(postMapper.deleteById(999L)).thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> postService.removePost(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Post not found");
    }

    private Post buildPost(Long id, String date, String title, String excerpt, String content) {
        Post post = new Post();
        post.setId(id);
        post.setPostDate(date);
        post.setTitle(title);
        post.setExcerpt(excerpt);
        post.setContent(content);
        return post;
    }
}