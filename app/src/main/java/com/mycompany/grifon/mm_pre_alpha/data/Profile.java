package com.mycompany.grifon.mm_pre_alpha.data;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class Profile {
    private String name;
    private String uuid;
    private String information;
    private final Map<String, PlainUser> subscribers = new HashMap<>(); //чтобы не падало это getSubscribers() если сабскрайберов нет у данного пользователя
    private final Map<String, PlainUser> subscriptions = new HashMap<>();
    //private final List<Post> userPlayList = new ArrayList<>(); // пока убрал свой плейлист, всё через посты
    private Map<String, Post> posts = new HashMap<>();
    private final Map<String,PlainChat> chats = new HashMap<>();

    public Profile() {}

    public Profile(String name, String uuid, String information, Map<String, PlainUser> subscribers, Map<String, PlainUser> subscriptions, Map<String, Post> posts, List<PlainChat> plainChat) {
        this.name = name;
        this.uuid = uuid;
        this.information = information;
        this.subscribers.putAll(subscribers);
        this.subscriptions.putAll(subscriptions);
        //this.userPlayList.addAll(userPlayList);
        this.posts.putAll(posts);
        this.chats.putAll(chats);
    }

    public Profile(@NonNull String name,@NonNull String uuid, String information) {
        this.name = name;
        this.uuid = uuid;
        this.information = information;

    }

    //empty profile
    public Profile(@NonNull String name, @NonNull String uuid){
        this.name = name;
        this.uuid = uuid;
        this.information = "Add information!";

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("uuid", uuid);
        result.put("information", information);
        result.put("subscribers", subscribers);
        result.put("subscriptions", subscriptions);
        //result.put("userPlayList", userPlayList);
        result.put("posts", posts);

        return result;
    }

    public PlainUser toPlain(){
        return new PlainUser(name,uuid);
    }

    public String getName(){return name;}
    public String getUuid(){return uuid;}
    public String getInformation(){return information;}
    public Map<String, PlainUser> getSubscribers(){return subscribers;}
    public Map<String, PlainUser> getSubscriptions(){return subscriptions;}
    //public List<Post> getUserPlayList(){return userPlayList;}
    public Map<String, Post> getPosts(){return posts;}
    // не работает корректно
    public int getMyPostsSize(){
        List<Post> myPosts = new ArrayList<>();
        for(Post postIter : posts.values()) {
            if (postIter.getAuthor() == null) {
                myPosts.add(postIter);
            }
        }
        return myPosts.size();
    }

    public void setSubscribers(Map<String, PlainUser> subscribers){
        this.subscribers.clear();
        this.subscribers.putAll(subscribers);
    }


    public Map<String,PlainChat> getChats() {
        return chats;
    }
    public PlainChat getPlainChatWithUser(PlainUser user) {
        for(PlainChat chat: chats.values()){
            if(chat.getUsers().contains(user)){
                return chat;
            }
        }
        return null;
    }
    public void setChats(Map<String,PlainChat> chats) {

        this.chats.clear();
        this.chats.putAll(chats);
    }
}
