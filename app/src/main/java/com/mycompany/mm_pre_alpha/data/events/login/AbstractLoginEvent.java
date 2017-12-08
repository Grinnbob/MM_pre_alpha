package com.mycompany.mm_pre_alpha.data.events.login;

/**
 * Created by Vlad on 15.11.2017.
 */

public abstract class AbstractLoginEvent {
    private final boolean isSucceed;
    private final String username;

    public AbstractLoginEvent(boolean isSucceed, String username) {
        this.isSucceed = isSucceed;
        this.username = username;
    }

    public boolean isSucceed() {
        return isSucceed;
    }

    public String getUsername() {
        return username;
    }
}
