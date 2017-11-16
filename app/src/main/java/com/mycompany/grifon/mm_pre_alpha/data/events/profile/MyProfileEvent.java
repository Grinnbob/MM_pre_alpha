package com.mycompany.grifon.mm_pre_alpha.data.events.profile;

import com.mycompany.grifon.mm_pre_alpha.data.Profile;

/**
 * Created by Vlad on 06.11.2017.
 */
public class MyProfileEvent {
    final private Profile profile;

    public MyProfileEvent(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }
}