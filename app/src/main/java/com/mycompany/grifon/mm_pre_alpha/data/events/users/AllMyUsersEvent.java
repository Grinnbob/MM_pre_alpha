package com.mycompany.grifon.mm_pre_alpha.data.events.users;

import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;

import java.util.List;

public class AllMyUsersEvent  {
    final private List<PlainUser> plainUsers;

    public AllMyUsersEvent(List<PlainUser> plainUsers) {
        this.plainUsers = plainUsers;
    }

    public List<PlainUser> getPlainUsers() {
        return plainUsers;
    }
}

