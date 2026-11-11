package com.portfolio.portfolio_api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.portfolio_api.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Checks the X-Admin-Token header for owner-only endpoints.
 * Returns 401 if the token is missing or wrong.
 *
 * @author Kewei
 * @date 2026-11-11
 */
public class AdminTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AdminTokenFilter.class);

    /** Header name that carries the admin token. */
    private static final String ADMIN_HEADER = "X-Admin-Token";

    /** HTTP method constants. */
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";

    /** Owner-only endpoint: list all messages. */
    private static final String PATH_MESSAGES_ALL = "/api/v1/messages/all";

    /** Owner-only endpoint prefix: blog post write operations. */
    private static final String PATH_POSTS_PREFIX = "/api/v1/posts";

    private final String adminToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdminTokenFilter(String adminToken) {
        this.adminToken = adminToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // Only owner-only endpoints need the token; other requests pass through
        if (isAdminEndpoint(request) && !adminToken.equals(request.getHeader(ADMIN_HEADER))) {
            // Token missing or wrong: log a warning (method + path for debugging) and return 401
            logger.warn("Admin auth failed: {} {} (missing or wrong {})",
                    request.getMethod(), request.getRequestURI(), ADMIN_HEADER);
            writeUnauthorized(response);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * Owner-only endpoints: GET /messages/all, POST/PUT/DELETE /posts.
     *
     * @param request the current request
     * @return true if the endpoint requires the admin token
     */
    private boolean isAdminEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (METHOD_GET.equals(method) && PATH_MESSAGES_ALL.equals(path)) {
            return true;
        }
        return (METHOD_POST.equals(method) || METHOD_PUT.equals(method) || METHOD_DELETE.equals(method))
                && path.startsWith(PATH_POSTS_PREFIX);
    }

    /**
     * Write a 401 JSON response.
     *
     * @param response the current response
     * @throws IOException if writing fails
     */
    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Missing or wrong X-Admin-Token");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}