package com.mycompany.mm_pre_alpha.data.events.profile;

import com.mycompany.mm_pre_alpha.data.Profile;

public class MyProfileEvent {
    final private Profile profile;

    public MyProfileEvent(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }
}