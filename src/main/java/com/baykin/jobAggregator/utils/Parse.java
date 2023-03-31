package com.baykin.jobAggregator.utils;

import com.baykin.jobAggregator.entity.Post;

import java.util.List;

public interface Parse {
    List<Post> list(String link, int amountPage);
}
