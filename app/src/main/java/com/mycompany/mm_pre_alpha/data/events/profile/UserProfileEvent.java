package com.mycompany.mm_pre_alpha.data.events.profile;

import com.mycompany.mm_pre_alpha.data.Profile;

public class UserProfileEvent {
    final private Profile profile;

    public UserProfileEvent(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }
}
