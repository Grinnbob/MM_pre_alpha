package com.mycompany.grifon.mm_pre_alpha.utils.domain;

import java.util.List;

public class Profile {
    final String name;
    final String uuid;
    final String information;
    final List<Profile> subscribers;
    final List<Profile> subscriptions;
    final List<SongInfo> userPlayList;
    final List<Post> posts;

    public Profile(String name, String uuid, String information, List<Profile> subscribers, List<Profile> subscriptions, List<SongInfo> userPlayList, List<Post> posts) {
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

    public String getName(){return name;}
    public String getUuid(){return uuid;}
    public String getInformation(){return information;}
    public List<Profile> getSubscribers(){return subscribers;}
    public List<Profile> getSubscriptions(){return subscriptions;}
    public List<SongInfo> getUserPlayList(){return userPlayList;}
    public List<Post> getPosts(){return posts;}
}
