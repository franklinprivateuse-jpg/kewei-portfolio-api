package com.portfolio.portfolio_api.mapper;

import com.portfolio.portfolio_api.entity.Message;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Persistence-layer tests for {@link MessageMapper} against an in-memory H2 database.
 * Each test runs in a rolled-back transaction (test isolation guaranteed by {@code @MybatisTest}).
 *
 * @author Kewei
 * @date 2026-11-11
 */
@MybatisTest
class MessageMapperTest {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    void should_setGeneratedId_when_insert() {
        // Arrange
        Message message = buildMessage("s1", "user", "hello");

        // Act
        messageMapper.insert(message);

        // Assert
        assertThat(message.getId()).isNotNull().isPositive();
    }

    @Test
    void should_persistAllFields_when_insert() {
        // Arrange
        Message message = buildMessage("s1", "user", "hello world");

        // Act
        messageMapper.insert(message);
        List<Message> result = messageMapper.listBySessionId("s1");

        // Assert
        assertThat(result).hasSize(1);
        Message saved = result.get(0);
        assertThat(saved.getSessionId()).isEqualTo("s1");
        assertThat(saved.getSender()).isEqualTo("user");
        assertThat(saved.getContent()).isEqualTo("hello world");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void should_returnOnlySessionMessages_when_listBySessionId() {
        // Arrange
        insertMessage("s1", "user", "hello from s1");
        insertMessage("s2", "user", "hello from s2");
        insertMessage("s1", "bot", "reply in s1");

        // Act
        List<Message> result = messageMapper.listBySessionId("s1");

        // Assert
        // Order-insensitive assertion: H2 TIMESTAMP has second-level precision,
        // so rows inserted in the same second may not have a stable order
        assertThat(result).hasSize(2)
                .allMatch(m -> "s1".equals(m.getSessionId()))
                .extracting(Message::getContent)
                .containsExactlyInAnyOrder("hello from s1", "reply in s1");
    }

    @Test
    void should_returnEmptyList_when_noMessagesForSession() {
        // Arrange
        // (no data inserted)

        // Act
        List<Message> result = messageMapper.listBySessionId("nonexistent");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void should_returnAllMessages_when_listAll() {
        // Arrange
        insertMessage("s1", "user", "msg1");
        insertMessage("s2", "bot", "msg2");

        // Act
        List<Message> result = messageMapper.listAll();

        // Assert
        assertThat(result).hasSize(2)
                .extracting(Message::getSessionId)
                .containsExactlyInAnyOrder("s1", "s2");
    }

    @Test
    void should_returnEmptyList_when_listAllAndNoData() {
        // Arrange
        // (no data inserted)

        // Act
        List<Message> result = messageMapper.listAll();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void should_deleteOnlySessionMessages_when_deleteBySessionId() {
        // Arrange
        insertMessage("s1", "user", "msg1");
        insertMessage("s1", "bot", "msg2");
        insertMessage("s2", "user", "msg3");

        // Act
        int deleted = messageMapper.deleteBySessionId("s1");

        // Assert
        assertThat(deleted).isEqualTo(2);
        assertThat(messageMapper.listBySessionId("s1")).isEmpty();
        assertThat(messageMapper.listAll()).hasSize(1);
    }

    @Test
    void should_returnZero_when_deleteNonexistentSession() {
        // Arrange
        // (no data inserted)

        // Act
        int deleted = messageMapper.deleteBySessionId("nonexistent");

        // Assert
        assertThat(deleted).isZero();
    }

    private Message buildMessage(String sessionId, String sender, String content) {
        Message message = new Message();
        message.setSessionId(sessionId);
        message.setSender(sender);
        message.setContent(content);
        return message;
    }

    private void insertMessage(String sessionId, String sender, String content) {
        messageMapper.insert(buildMessage(sessionId, sender, content));
    }
}