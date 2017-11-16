package com.mycompany.grifon.mm_pre_alpha.data.events.chat;

import com.mycompany.grifon.mm_pre_alpha.engine.events.EventWithSingleArg;
import com.mycompany.grifon.mm_pre_alpha.data.Chat;

/**
 * Created by Vlad on 13.11.2017.
 */

public class WholeChatEvent extends EventWithSingleArg<Chat> {

    public WholeChatEvent(Chat arg) {
        super(arg);
    }
}
