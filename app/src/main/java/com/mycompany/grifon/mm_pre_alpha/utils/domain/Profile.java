package com.mycompany.grifon.mm_pre_alpha.utils.domain;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile /*implements Serializable*/{
    private String name;
    private String uuid;
    private String information;
    private Map<String, PlainUser> subscribers = new HashMap<>();//чтобы не падало это getSubscribers() если сабскрайберов нет у данного пользователя
    //private Map<String, PlainUser> subscribers;
    private Map<String, PlainUser> subscriptions;
    private List<Post> userPlayList;
    private List<Post> posts;

    Profile() {
    }


    public Profile(String name, String uuid, String information, Map<String, PlainUser> subscribers, Map<String, PlainUser> subscriptions, List<Post> userPlayList, List<Post> posts) {
        this.name = name;
        this.uuid = uuid;
        this.information = information;
        this.subscribers = subscribers;
        this.subscriptions = subscriptions;
        this.userPlayList = userPlayList;
        this.posts = posts;
    }

    //empty profile
    public Profile(String name, String uuid){
        this.name = name;
        this.uuid = uuid;
        this.information = "Add information!";
        this.subscribers = null;
        this.subscriptions = null;
        this.userPlayList = null;
        this.posts = null;
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
    public Map<String, PlainUser> getSubscribers(){return subscribers;}
    //public Map<String, PlainUser> getSubscribers(){return subscribers;}
    public Map<String, PlainUser> getSubscriptions(){return subscriptions;}
    public List<Post> getUserPlayList(){return userPlayList;}
    public List<Post> getPosts(){return posts;}

    public void setSubscribers(Map<String, PlainUser> subscribers){this.subscribers = subscribers;}



}
