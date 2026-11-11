package com.portfolio.portfolio_api.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AdminTokenFilter}.
 * Uses Spring's mock servlet objects; {@link FilterChain} is mocked.
 *
 * @author Kewei
 * @date 2026-11-11
 */
@ExtendWith(MockitoExtension.class)
class AdminTokenFilterTest {

    private static final String VALID_TOKEN = "123456";
    private static final String ADMIN_HEADER = "X-Admin-Token";

    @Mock
    private FilterChain filterChain;

    // Constructor injection (not @InjectMocks) because the token is a plain string
    private final AdminTokenFilter filter = new AdminTokenFilter(VALID_TOKEN);

    @Test
    void should_callChain_when_requestIsNotAdminEndpoint() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/messages");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert — visitor endpoints pass through without any token check
        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void should_return401_when_adminEndpointWithoutToken() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/messages/all");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert — request is blocked before reaching the controller
        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).contains("Missing or wrong X-Admin-Token");
    }

    @Test
    void should_return401_when_adminEndpointWithWrongToken() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/messages/all");
        request.addHeader(ADMIN_HEADER, "wrong-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert — a wrong token is treated the same as a missing one
        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void should_callChain_when_adminEndpointWithCorrectToken() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/messages/all");
        request.addHeader(ADMIN_HEADER, VALID_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert — valid token lets the request through to the controller
        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}