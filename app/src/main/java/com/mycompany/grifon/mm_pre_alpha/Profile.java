package com.mycompany.grifon.mm_pre_alpha;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Profile {
    final String name;
    final String information;
    final List<Profile> subscribers;
    final List<Profile> subscriptions;

    public Profile(String name, String information, List<Profile> subscribers, List<Profile> subscriptions) {
        this.name = name;
        this.information = information;
        this.subscribers = subscribers;
        this.subscriptions = subscriptions;
    }
}
