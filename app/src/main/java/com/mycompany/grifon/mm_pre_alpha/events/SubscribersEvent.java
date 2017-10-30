package com.mycompany.grifon.mm_pre_alpha.events;

import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;

import java.util.List;

/**
 * Created by Vlad on 27.10.2017.
 */

public class SubscribersEvent  {
    final private List<PlainUser> plainUsers;

    public SubscribersEvent(List<PlainUser> plainUsers) {
        this.plainUsers = plainUsers;
    }

    public List<PlainUser> getPlainUsers() {
        return plainUsers;
    }
}
