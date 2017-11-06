package com.mycompany.grifon.mm_pre_alpha.utils.domain;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile {
    public String name;
    public String uuid;
    public String information;
    public List<Profile> subscribers;
    public List<Profile> subscriptions;
    public List<Post> userPlayList;
    public List<Post> posts;

    private Profile(){}

    public Profile(String name, String uuid, String information, List<Profile> subscribers, List<Profile> subscriptions, List<Post> userPlayList, List<Post> posts) {
        this.name = name;
        this.uuid = uuid;
        this.information = information;
        this.subscribers = subscribers;
        this.subscriptions = subscriptions;
        this.userPlayList = userPlayList;
        this.posts = posts;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("uuid", uuid);
        result.put("information", information);
        result.put("subscribers", subscribers);
        result.put("subscriptions", subscriptions);
        result.put("userPlayList", userPlayList);
        result.put("posts", posts);

        return result;
    }

    public String getName(){return name;}
    public String getUuid(){return uuid;}
    public String getInformation(){return information;}
    public List<Profile> getSubscribers(){return subscribers;}
    public List<Profile> getSubscriptions(){return subscriptions;}
    public List<Post> getUserPlayList(){return userPlayList;}
    public List<Post> getPosts(){return posts;}
}
