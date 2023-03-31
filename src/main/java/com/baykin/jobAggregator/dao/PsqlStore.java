package com.baykin.jobAggregator.dao;

import com.baykin.jobAggregator.entity.Post;
import com.baykin.jobAggregator.utils.ConnectionManager;
import com.baykin.jobAggregator.utils.PropertiesUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PsqlStore implements Store {
    private Connection cn;

    public PsqlStore(PropertiesUtil propertyPath) {
        cn = new ConnectionManager(propertyPath).open();
    }

    @Override
    public void save(Post post) {
        try (var statement = cn.prepareStatement("""
                    INSERT INTO post (title, link, description, created)
                    VALUES (?,?,?,?)
                    ON CONFLICT (link) DO NOTHING
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getDescription());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                post.setId(generatedKeys.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (var statement = cn.prepareStatement("""
                    SELECT id, title, link, description, created
                    FROM post;
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.executeQuery();
            ResultSet setPosts = statement.getResultSet();
            while (setPosts.next()) {
                posts.add(buildPost(setPosts));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return posts;
    }

    @Override
    public Optional<Post> findById(int id) {
        Post post = null;
        try (var statement = cn.prepareStatement(
                """
                        SELECT id, title, link, description, created
                        FROM post
                        WHERE id = ?;
                        """)) {
            statement.setInt(1, id);
            var setPosts = statement.executeQuery();
            if (setPosts.next()) {
                post = new Post(setPosts.getInt("id"),
                        setPosts.getString("title"),
                        setPosts.getString("link"),
                        setPosts.getString("description"),
                        setPosts.getTimestamp("created").toLocalDateTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(post);
    }

    private Post buildPost(ResultSet resultSet) throws SQLException {
        return new Post(resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("link"),
                resultSet.getString("description"),
                resultSet.getTimestamp("created").toLocalDateTime());
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }
}
