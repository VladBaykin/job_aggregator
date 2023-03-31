package com.baykin.jobAggregator.dao;

import com.baykin.jobAggregator.entity.Post;

import java.util.List;
import java.util.Optional;

public interface Store extends AutoCloseable {
    void save(Post post);
    List<Post> getAll();
    Optional<Post> findById(int id);
}
