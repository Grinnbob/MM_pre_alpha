package com.mycompany.grifon.mm_pre_alpha.data.events.profile;

import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;

import java.util.Map;

public class SubscribersEvent {
    final private Map<String, PlainUser> plainUsers;

    public SubscribersEvent(Map<String, PlainUser> plainUsers) {
        this.plainUsers = plainUsers;
    }

    public Map<String, PlainUser> getSubscribers() {
        return plainUsers;
    }
}
