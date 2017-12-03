package com.mycompany.grifon.mm_pre_alpha.data;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Chat implements Comparable<Chat> {
    private String name;
    private String uuid;
    private final  Map<String,PlainUser> users=new HashMap<>();
    private final  Map<String,Message> messages=new HashMap<>();

    public Chat() {
    }

    public Chat(PlainChat plainChat) {
        this.name = plainChat.getName();
        this.uuid = plainChat.getUuid();
        for(PlainUser user:plainChat.getUsers()){
            users.put(user.getUuid(),user);
        }
    }

    public Chat(String name, String uuid, Map<String,PlainUser> users, Map<String,Message> messages) {
        this.name = name;
        this.uuid = uuid;
        this.users.putAll(users);
        this.messages.putAll(messages);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String,PlainUser> getUsers() {
        return users;
    }

    public void setUsers(Map<String,PlainUser> users) {
        this.users.clear();
        this.users.putAll(users);
    }

    public Map<String,Message> getMessages() {
        return messages;
    }

    public void setMessages(Map<String,Message> messages) {
        this.messages.clear();
        this.messages.putAll(messages);
    }

    @Override
    public int compareTo(@NonNull Chat o) {
        return 0;
    }
}
