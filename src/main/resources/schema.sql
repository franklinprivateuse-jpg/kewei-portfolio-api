-- Messages table (chat between visitor and bot)
CREATE TABLE IF NOT EXISTS messages (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(64)  NOT NULL,
    sender     VARCHAR(10)  NOT NULL,          -- 'user' or 'bot'
    content    VARCHAR(1000) NOT NULL,          -- maps to API field 'text'
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Blog posts table
CREATE TABLE IF NOT EXISTS blog_posts (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_date  VARCHAR(10)  NOT NULL,          -- yyyy-MM-dd, maps to API field 'date'
    title      VARCHAR(200) NOT NULL,
    excerpt    VARCHAR(500),
    content    CLOB,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);