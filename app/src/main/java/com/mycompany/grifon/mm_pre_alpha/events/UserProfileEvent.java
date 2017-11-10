package com.mycompany.grifon.mm_pre_alpha.events;

import com.mycompany.grifon.mm_pre_alpha.utils.domain.Profile;

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
