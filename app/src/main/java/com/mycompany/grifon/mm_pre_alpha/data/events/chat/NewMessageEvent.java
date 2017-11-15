package com.mycompany.grifon.mm_pre_alpha.data.events.chat;

import com.mycompany.grifon.mm_pre_alpha.engine.events.EventWithSingleArg;
import com.mycompany.grifon.mm_pre_alpha.data.Message;

/**
 * Created by Vlad on 13.11.2017.
 */

public class NewMessageEvent extends EventWithSingleArg<Message> {
    public NewMessageEvent(Message arg) {
        super(arg);
    }
}
