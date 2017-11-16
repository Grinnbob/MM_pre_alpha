package com.mycompany.grifon.mm_pre_alpha.data.events.profile;

import com.mycompany.grifon.mm_pre_alpha.data.Profile;

/**
 * Created by Vlad on 06.11.2017.
 */

public class UserProfileEvent {
    final private Profile profile;

    public UserProfileEvent(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }
}
