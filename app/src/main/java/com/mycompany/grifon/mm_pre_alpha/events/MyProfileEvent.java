package com.mycompany.grifon.mm_pre_alpha.events;

import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.Profile;

import java.util.List;

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