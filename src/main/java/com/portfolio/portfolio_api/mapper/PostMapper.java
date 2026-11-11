package com.portfolio.portfolio_api.mapper;

import com.portfolio.portfolio_api.entity.Post;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * MyBatis mapper for the blog_posts table.
 * Uses snake_case auto-mapping (post_date -> postDate).
 *
 * @author Kewei
 * @date 2026-11-11
 */
@Mapper
public interface PostMapper {

    /**
     * Insert a blog post.
     *
     * @param post the post to insert (id is auto-generated and set back)
     */
    @Insert("INSERT INTO blog_posts (post_date, title, excerpt, content) VALUES (#{postDate}, #{title}, #{excerpt}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Post post);

    /**
     * List all posts without the content column (list view).
     * Newest posts first.
     *
     * @return all posts; empty list if none
     */
    @Select("SELECT id, post_date, title, excerpt FROM blog_posts ORDER BY id DESC")
    List<Post> listAll();

    /**
     * Find one post by id, including full content.
     *
     * @param id the post id
     * @return the post, or null if not found
     */
    @Select("SELECT id, post_date, title, excerpt, content, created_at FROM blog_posts WHERE id = #{id}")
    Post findById(Long id);

    /**
     * Update a post by id.
     *
     * @param post the post fields to update (id identifies the row)
     * @return number of affected rows (0 if the id does not exist)
     */
    @Update("UPDATE blog_posts SET post_date = #{postDate}, title = #{title}, excerpt = #{excerpt}, content = #{content} WHERE id = #{id}")
    int updateById(Post post);

    /**
     * Delete a post by id.
     *
     * @param id the post id
     * @return number of deleted rows (0 if the id does not exist)
     */
    @Delete("DELETE FROM blog_posts WHERE id = #{id}")
    int deleteById(Long id);
}