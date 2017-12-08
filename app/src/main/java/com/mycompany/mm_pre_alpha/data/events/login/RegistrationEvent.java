package com.mycompany.mm_pre_alpha.data.events.login;

public class RegistrationEvent extends AbstractLoginEvent{

    public RegistrationEvent(boolean isSucceed, String username) {
        super(isSucceed, username);
    }
}
