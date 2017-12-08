package com.mycompany.mm_pre_alpha.data.events.chat;

import com.mycompany.mm_pre_alpha.engine.events.EventWithSingleArg;
import com.mycompany.mm_pre_alpha.data.Message;

/**
 * Created by Vlad on 13.11.2017.
 */

public class RemoveMessageEvent extends EventWithSingleArg<Message> {
    public RemoveMessageEvent(Message arg) {
        super(arg);
    }
}
