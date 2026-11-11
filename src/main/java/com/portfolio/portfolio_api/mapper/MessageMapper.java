package com.portfolio.portfolio_api.mapper;

import com.portfolio.portfolio_api.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * MyBatis mapper for the messages table.
 * Uses snake_case auto-mapping (session_id -> sessionId).
 *
 * @author Kewei
 * @date 2026-11-11
 */
@Mapper
public interface MessageMapper {

    /**
     * Insert a chat message.
     *
     * @param message the message to insert (id is auto-generated and set back)
     */
    @Insert("INSERT INTO messages (session_id, sender, content) VALUES (#{sessionId}, #{sender}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Message message);

    /**
     * List all messages of one session, ordered by creation time.
     *
     * @param sessionId the chat session id
     * @return message list of the session; empty list if none
     */
    @Select("SELECT id, session_id, sender, content, created_at FROM messages WHERE session_id = #{sessionId} ORDER BY created_at")
    List<Message> listBySessionId(String sessionId);

    /**
     * List all messages, ordered by creation time.
     *
     * @return all messages; empty list if none
     */
    @Select("SELECT id, session_id, sender, content, created_at FROM messages ORDER BY created_at")
    List<Message> listAll();

    /**
     * Delete all messages of one session.
     *
     * @param sessionId the chat session id
     * @return number of deleted rows
     */
    @Delete("DELETE FROM messages WHERE session_id = #{sessionId}")
    int deleteBySessionId(String sessionId);
}