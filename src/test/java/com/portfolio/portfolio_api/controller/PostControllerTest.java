package com.portfolio.portfolio_api.controller;

import com.portfolio.portfolio_api.dto.PostDetailVO;
import com.portfolio.portfolio_api.dto.PostVO;
import com.portfolio.portfolio_api.exception.NotFoundException;
import com.portfolio.portfolio_api.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for {@link PostController}.
 * {@link PostService} is mocked; filters are disabled to isolate the controller.
 *
 * @author Kewei
 * @date 2026-11-11
 */
@WebMvcTest(PostController.class)
// Disable filters to isolate the controller layer (skip AdminTokenFilter/SecurityFilter).
// Admin auth logic is covered by AdminTokenFilter's own tests.
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void should_returnPostList_when_listPosts() throws Exception {
        // Arrange
        when(postService.listAllPosts()).thenReturn(List.of(
                new PostVO(1L, "2026-11-11", "First Post", "Summary 1"),
                new PostVO(2L, "2026-11-12", "Second Post", "Summary 2")));

        // Act & Assert
        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].date").value("2026-11-11"))
                .andExpect(jsonPath("$[0].title").value("First Post"))
                .andExpect(jsonPath("$[0].excerpt").value("Summary 1"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void should_returnDetail_when_getPostExists() throws Exception {
        // Arrange
        when(postService.getPost(1L)).thenReturn(
                new PostDetailVO(1L, "2026-11-11", "My Post", "Summary", "Full body"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.date").value("2026-11-11"))
                .andExpect(jsonPath("$.title").value("My Post"))
                .andExpect(jsonPath("$.excerpt").value("Summary"))
                .andExpect(jsonPath("$.content").value("Full body"));
    }

    @Test
    void should_return404_when_getPostNotExists() throws Exception {
        // Arrange
        when(postService.getPost(999L)).thenThrow(new NotFoundException("Post not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/posts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Post not found"));
    }

    @Test
    void should_return201WithDetail_when_createPost() throws Exception {
        // Arrange
        when(postService.createPost(any())).thenReturn(
                new PostDetailVO(1L, "2026-11-11", "New Post", "Summary", "Body"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2026-11-11\",\"title\":\"New Post\",\"excerpt\":\"Summary\",\"content\":\"Body\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Post"))
                .andExpect(jsonPath("$.content").value("Body"));
    }

    @Test
    void should_return400_when_titleIsBlank() throws Exception {
        // Arrange
        String body = "{\"date\":\"2026-11-11\",\"title\":\"\",\"excerpt\":\"Summary\",\"content\":\"Body\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void should_return400_when_dateFormatInvalid() throws Exception {
        // Arrange
        String body = "{\"date\":\"11/11/2026\",\"title\":\"Title\",\"excerpt\":\"Summary\",\"content\":\"Body\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void should_return200WithDetail_when_updatePost() throws Exception {
        // Arrange
        when(postService.updatePost(eq(1L), any())).thenReturn(
                new PostDetailVO(1L, "2026-11-12", "Updated", "New summary", "New body"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2026-11-12\",\"title\":\"Updated\",\"excerpt\":\"New summary\",\"content\":\"New body\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.content").value("New body"));
    }

    @Test
    void should_return404_when_updatePostNotExists() throws Exception {
        // Arrange
        when(postService.updatePost(eq(999L), any())).thenThrow(new NotFoundException("Post not found"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/posts/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2026-11-11\",\"title\":\"Title\",\"excerpt\":\"Summary\",\"content\":\"Body\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void should_return204_when_removePost() throws Exception {
        // Arrange
        // (no stubbing needed — void method)

        // Act & Assert
        mockMvc.perform(delete("/api/v1/posts/1"))
                .andExpect(status().isNoContent());
        verify(postService).removePost(1L);
    }

    @Test
    void should_return404_when_removePostNotExists() throws Exception {
        // Arrange
        doThrow(new NotFoundException("Post not found")).when(postService).removePost(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/posts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}