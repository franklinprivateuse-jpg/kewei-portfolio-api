package com.portfolio.portfolio_api.service;

import com.portfolio.portfolio_api.dto.AdminMessageVO;
import com.portfolio.portfolio_api.dto.ChatResponse;
import com.portfolio.portfolio_api.dto.MessageRequest;
import com.portfolio.portfolio_api.dto.MessageVO;
import com.portfolio.portfolio_api.entity.Message;
import com.portfolio.portfolio_api.mapper.MessageMapper;
import com.portfolio.portfolio_api.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MessageServiceImpl}.
 * Business logic only; {@link MessageMapper} is mocked.
 *
 * @author Kewei
 * @date 2026-11-11
 */
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void should_saveUserAndBotMessage_when_sendMessage() {
        // Arrange
        MessageRequest request = new MessageRequest("session-1", "hello there");
        // Simulate DB auto-increment: first insert gets id=1, second gets id=2 (user then bot)
        doAnswer(invocation -> {
            invocation.<Message>getArgument(0).setId(1L);
            return null;
        }).doAnswer(invocation -> {
            invocation.<Message>getArgument(0).setId(2L);
            return null;
        }).when(messageMapper).insert(any(Message.class));

        // Act
        ChatResponse response = messageService.sendMessage(request);

        // Assert
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageMapper, times(2)).insert(captor.capture());
        List<Message> saved = captor.getAllValues();
        assertThat(saved.get(0).getSessionId()).isEqualTo("session-1");
        assertThat(saved.get(0).getSender()).isEqualTo("user");
        assertThat(saved.get(0).getContent()).isEqualTo("hello there");
        assertThat(saved.get(1).getSender()).isEqualTo("bot");
        assertThat(response.userMessage()).isEqualTo(new MessageVO(1L, "hello there", "user"));
        assertThat(response.botMessage().id()).isEqualTo(2L);
        assertThat(response.botMessage().sender()).isEqualTo("bot");
    }

    @Test
    void should_matchHelloReply_when_textContainsHelloKeyword() {
        // Arrange
        MessageRequest request = new MessageRequest("s", "hello");
        stubInsertWithIds();

        // Act
        ChatResponse response = messageService.sendMessage(request);

        // Assert
        assertThat(response.botMessage().text())
                .isEqualTo("Hello! Thanks for visiting my portfolio. How can I help you?");
    }

    @Test
    void should_matchSkillReply_when_textContainsSkillKeyword() {
        // Arrange
        MessageRequest request = new MessageRequest("s", "what is your tech stack?");
        stubInsertWithIds();

        // Act
        ChatResponse response = messageService.sendMessage(request);

        // Assert
        assertThat(response.botMessage().text())
                .isEqualTo("My main skills include Java, TypeScript and React — see the Knowledge page for the full list.");
    }

    @Test
    void should_returnDefaultReply_when_noKeywordMatches() {
        // Arrange
        MessageRequest request = new MessageRequest("s", "zzz qqq xxx");
        stubInsertWithIds();

        // Act
        ChatResponse response = messageService.sendMessage(request);

        // Assert
        assertThat(response.botMessage().text()).startsWith("Thanks for your message!");
    }

    @Test
    void should_beCaseInsensitive_when_matchingKeywords() {
        // Arrange
        MessageRequest request = new MessageRequest("s", "HELLO WORLD");
        stubInsertWithIds();

        // Act
        ChatResponse response = messageService.sendMessage(request);

        // Assert
        assertThat(response.botMessage().text()).contains("Hello!");
    }

    @Test
    void should_returnEmptyList_when_noChatHistory() {
        // Arrange
        when(messageMapper.listBySessionId("empty-session")).thenReturn(List.of());

        // Act
        List<MessageVO> result = messageService.listChatHistory("empty-session");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void should_returnVisitorViews_when_listChatHistory() {
        // Arrange
        Message entity = new Message();
        entity.setId(10L);
        entity.setSessionId("session-1");
        entity.setSender("user");
        entity.setContent("hi");
        entity.setCreatedAt(LocalDateTime.now());
        when(messageMapper.listBySessionId("session-1")).thenReturn(List.of(entity));

        // Act
        List<MessageVO> result = messageService.listChatHistory("session-1");

        // Assert
        assertThat(result).containsExactly(new MessageVO(10L, "hi", "user"));
    }

    @Test
    void should_returnAdminViews_when_listAllMessages() {
        // Arrange
        Message entity = new Message();
        entity.setId(11L);
        entity.setSessionId("session-1");
        entity.setSender("bot");
        entity.setContent("reply");
        entity.setCreatedAt(LocalDateTime.of(2026, 11, 11, 10, 0));
        when(messageMapper.listAll()).thenReturn(List.of(entity));

        // Act
        List<AdminMessageVO> result = messageService.listAllMessages();

        // Assert
        assertThat(result).hasSize(1);
        AdminMessageVO vo = result.get(0);
        assertThat(vo.id()).isEqualTo(11L);
        assertThat(vo.sessionId()).isEqualTo("session-1");
        assertThat(vo.text()).isEqualTo("reply");
        assertThat(vo.sender()).isEqualTo("bot");
        assertThat(vo.createdAt()).isEqualTo(LocalDateTime.of(2026, 11, 11, 10, 0));
    }

    @Test
    void should_callDeleteBySessionId_when_removeChatHistory() {
        // Arrange
        when(messageMapper.deleteBySessionId("session-1")).thenReturn(2);

        // Act
        messageService.removeChatHistory("session-1");

        // Assert
        verify(messageMapper).deleteBySessionId("session-1");
    }

    /** Simulate the DB assigning auto-increment ids on insert. */
    private void stubInsertWithIds() {
        doAnswer(invocation -> {
            invocation.<Message>getArgument(0).setId(1L);
            return null;
        }).doAnswer(invocation -> {
            invocation.<Message>getArgument(0).setId(2L);
            return null;
        }).when(messageMapper).insert(any(Message.class));
    }
}