package com.mycompany.mm_pre_alpha.data.events.login;

/**
 * Created by Vlad on 15.11.2017.
 */

public class RegistrationEvent extends AbstractLoginEvent{

    public RegistrationEvent(boolean isSucceed, String username) {
        super(isSucceed, username);
    }
}
