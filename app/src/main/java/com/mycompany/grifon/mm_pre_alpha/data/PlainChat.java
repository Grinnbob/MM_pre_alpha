package com.mycompany.grifon.mm_pre_alpha.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlainChat implements Serializable{
    private String name;
    private String uuid;
    private List<PlainUser> users;

    public PlainChat() {
    }

    public PlainChat(Chat chat) {
        this.name = chat.getName();
        this.uuid = chat.getUuid();
        this.users =new ArrayList<>(chat.getUsers().values());
    }

    public PlainChat(String name, String uuid, List<PlainUser> users) {
        this.name = name;
        this.uuid = uuid;
        this.users =new ArrayList<>(users);
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

    public List<PlainUser> getUsers() {
        return users;
    }

    public void setUsers(List<PlainUser> users) {
        this.users = users;
    }
}
