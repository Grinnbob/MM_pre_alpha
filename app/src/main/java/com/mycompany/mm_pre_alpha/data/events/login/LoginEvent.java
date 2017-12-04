package com.mycompany.mm_pre_alpha.data.events.login;

/**
 * Created by Vlad on 15.11.2017.
 */

public class LoginEvent extends AbstractLoginEvent{

    public LoginEvent(boolean isSucceed, String username) {
        super(isSucceed, username);
    }
}
