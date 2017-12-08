package com.mycompany.grifon.mm_pre_alpha.events;

import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.engine.events.EventWithSingleArg;

import java.util.List;

public class Events {
    public static class NamesEvent extends EventWithSingleArg<List<PlainUser>> {
        public NamesEvent(List<PlainUser> arg) {
            super(arg);
        }
    }

    public static class SubscribersEvent extends EventWithSingleArg<List<PlainUser>> {
        public SubscribersEvent(List<PlainUser> arg) {
            super(arg);
        }
    }

    /*public static class SubscriptionsEvent extends EventWithSingleArg<List<PlainUser>>  {
        public SubscriptionsEvent(List<PlainUser> arg) {
            super(arg);
        }
    }*/

    public static class AllUsersEvent extends EventWithSingleArg<List<PlainUser>> {
        public AllUsersEvent(List<PlainUser> arg) {
            super(arg);
        }
    }

    /*public static class MyProfileEvent extends EventWithSingleArg<Profile>  {
        public MyProfileEvent(Profile arg) {
            super(arg);
        }
    }*/
}
