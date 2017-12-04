package com.mycompany.mm_pre_alpha.data.events.profile;

import com.mycompany.mm_pre_alpha.data.Post;

import java.util.Map;

public class PostsEvent {
    final private Map<String, Post> posts;

    public PostsEvent(Map<String, Post> posts) {
        this.posts = posts;
    }

    public Map<String, Post> getPosts() {
        return posts;
    }
}
