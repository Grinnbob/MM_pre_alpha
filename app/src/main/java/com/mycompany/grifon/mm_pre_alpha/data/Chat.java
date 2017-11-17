package com.mycompany.grifon.mm_pre_alpha.data;

import java.util.Map;

public class Chat {
    private String name;
    private String uuid;
    private Map<String,PlainUser> users;
    private Map<String,Message> messages;

    public Chat() {
    }

    public Chat(String name, String uuid, Map<String,PlainUser> users, Map<String,Message> messages) {
        this.name = name;
        this.uuid = uuid;
        this.users = users;
        this.messages = messages;
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
        this.users = users;
    }

    public Map<String,Message> getMessages() {
        return messages;
    }

    public void setMessages(Map<String,Message> messages) {
        this.messages = messages;
    }
}
