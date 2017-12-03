package com.mycompany.grifon.mm_pre_alpha.engine.firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mycompany.grifon.mm_pre_alpha.data.Chat;
import com.mycompany.grifon.mm_pre_alpha.data.Message;
import com.mycompany.grifon.mm_pre_alpha.data.PlainChat;
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.data.Post;
import com.mycompany.grifon.mm_pre_alpha.data.Profile;
import com.mycompany.grifon.mm_pre_alpha.data.SongInfo;
import com.mycompany.grifon.mm_pre_alpha.data.events.users.AllMyUsersEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.profile.UserProfileEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.profile.MyProfileEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.profile.SubscribersEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.chat.ChangeMessageEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.chat.NewMessageEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.chat.RemoveMessageEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.chat.WholeChatEvent;

import org.greenrobot.eventbus.EventBus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirebasePathHelper {

    private static FirebasePathHelper INSTANCE;

    private FirebasePathHelper() {
    }

    public static FirebasePathHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebasePathHelper();
        }
        return INSTANCE;
    }

    private ValueEventListener myProfileListener = new ValueEventListener() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Profile profile = dataSnapshot.getValue(Profile.class);
            //event fired!
            if ((user.getUid()).equals(profile.getUuid())) {
                EventBus.getDefault().post(new MyProfileEvent(profile));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener userProfileListener = new ValueEventListener() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Profile profile = dataSnapshot.getValue(Profile.class);
            //event fired!
            if (!(user.getUid()).equals(profile.getUuid())) {
                EventBus.getDefault().post(new UserProfileEvent(profile));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    // --------------------------------------------------------------------------------------------

    private static volatile DatabaseReference root = null;

    private DatabaseReference getRoot() {
        if (root == null)
            synchronized (FirebasePathHelper.class) {
                if (root == null) {
                    DatabaseReference local = FirebaseDatabase.getInstance().getReference();
                    root = local;
                }
            }
        return root;
    }

    // upload profile in Database (alternative)
    public void uploadProfileDB(Profile info) {
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("users/" + info.getUuid(), info.toMap());
        getRoot().updateChildren(childUpdates);
    }

    private DatabaseReference getUserData(String path) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return getRoot().child("users").child(user.getUid()).child(path);
    }

    public void updatePosts(Map<String, Post> posts) {
        //public Profile(String name, String uuid, String information, List<Profile> subscribers, List<Profile> subscriptions, List<SongInfo> userPlayList, List<Post> posts) {
        //  Profile newProfile = new Profile(info.getName(), info.getUuid(), info.getInformation(), subscribers, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //getUserData("posts")
        getUserData("posts").setValue(posts);
    }


    public void requestSubscribers(String uuid) {
        //getRoot().child("users").child(uuid).addValueEventListener(new ValueEventListener() {
        getRoot().child("users").child(uuid).child("subscribers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, PlainUser> plainUsers = new HashMap<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlainUser plainUser = postSnapshot.getValue(PlainUser.class);
                    plainUsers.put(plainUser.getUuid(), plainUser);
                    Log.e("Get Data", plainUser.toString());
                }
                //event fired!
                EventBus.getDefault().post(new SubscribersEvent(plainUsers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void requestAllUsers() {
        getRoot().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PlainUser> plainUsers = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlainUser plainUser = postSnapshot.getValue(PlainUser.class);
                    plainUsers.add(plainUser);
                    Log.e("Get Data", plainUser.toString());
                }
                //event fired!
                EventBus.getDefault().post(new AllMyUsersEvent(plainUsers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void requestSearchedUsers(final String searchedName) {
        getRoot().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PlainUser> plainUsers = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlainUser plainUser = postSnapshot.getValue(PlainUser.class);
                    if (plainUser.getName().toLowerCase().contains(searchedName.toLowerCase()))
                        plainUsers.add(plainUser);
                    Log.e("Get Data", plainUser.toString());
                }
                //event fired!
                EventBus.getDefault().post(new AllMyUsersEvent(plainUsers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getMyProfile(String uuid) {
        //return getRoot().child(String.valueOf(R.string.users_path)).child(user.getUid()).child(path);
        getRoot().child("users").child(uuid).removeEventListener(myProfileListener);
        getRoot().child("users").child(uuid).addValueEventListener(myProfileListener);
    }

    public void getUserProfile(final String uuid) {
        //return getRoot().child(String.valueOf(R.string.users_path)).child(user.getUid()).child(path);
        getRoot().child("users").child(uuid).removeEventListener(userProfileListener);
        getRoot().child("users").child(uuid).addValueEventListener(userProfileListener);
    }

    public void getChat(String uuid) {
        //return getRoot().child(String.valueOf(R.string.users_path)).child(user.getUid()).child(path);
        getRoot().child("chats").child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                //event fired!
                EventBus.getDefault().post(new WholeChatEvent(chat));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public DatabaseReference getChatMessagesReference(String uuid) {
        return getRoot().child("chats").child(uuid).child("messages").orderByChild("timeMessage").getRef();
    }

    public void subscribeToNewMessages(String uuid) {
        //return getRoot().child(String.valueOf(R.string.users_path)).child(user.getUid()).child(path);
        getRoot().child("chats").child(uuid).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message msg = dataSnapshot.getValue(Message.class);
                //event fired!
                EventBus.getDefault().post(new NewMessageEvent(msg));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Message msg = dataSnapshot.getValue(Message.class);
                //event fired!
                EventBus.getDefault().post(new ChangeMessageEvent(msg));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Message msg = dataSnapshot.getValue(Message.class);
                //event fired!
                EventBus.getDefault().post(new RemoveMessageEvent(msg));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // пишем new profile в Database
    public void writeNewProfileDB(Profile info) {
        getRoot().child("users").child(info.getUuid()).setValue(info);
    }

    // пишем new post в Database in my profile
    public void writeNewPostDB(String uuid, Post post) {
        //String key = getRoot().getKey();
        // use timestamps
        getRoot().child("users").child(uuid).child("posts").child(post.getUuid()).setValue(post);
    }

    public void deletePostDB(String uuid, Post post) {
        //String key = getRoot().getKey();
        // use timestamps
        getRoot().child("users").child(uuid).child("posts").child(post.getUuid()).setValue(null);
    }

    // пишем new post в Database in subscribers profiles
    public void writeNewPostToSubscribersDB(String uuid, final Post post) {
        getRoot().child("users").child(uuid).child("subscribers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlainUser plainUser = postSnapshot.getValue(PlainUser.class);
                    writeNewPostDB(plainUser.getUuid(), post);
                }
                //event fired!
                //EventBus.getDefault().post(new SubscribersEvent(plainUsers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // add like
    public void addLikeDB(String uuid, String timestamp, String likes) {
        getRoot().child("users").child(uuid).child("posts").child(timestamp).child("song").child("likes").setValue(likes);
    }

    // пишем new profile в Database
    public void createChat(PlainChat plainChat) {
        Chat chat = new Chat(plainChat);
        createChat(chat);
    }

    // пишем new profile в Database
    public void createChat(Chat chat) {
        //getRoot().getDatabase()
        for (PlainUser user : chat.getUsers().values()) {
            getRoot().child("users").child(user.getUuid()).child("chats").child(chat.getUuid()).setValue(new PlainChat(chat));
        }
        getRoot().child("chats").child(chat.getUuid()).setValue(chat);
    }

    public void updateChat(Chat chat) {
        //public Profile(String name, String uuid, String information, List<Profile> subscribers, List<Profile> subscriptions, List<SongInfo> userPlayList, List<Post> posts) {
        //  Profile newProfile = new Profile(info.getName(), info.getUuid(), info.getInformation(), subscribers, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        getRoot().child("chats").child(chat.getUuid()).setValue(chat);
    }

    public void addMessageToChat(String chatUUID, Message message) {
        getRoot().child("chats").child(chatUUID).child("messages").child(message.getUuid()).setValue(message);
    }

    public DatabaseReference getMyMusic() {
        return getUserData("music");
    }

    public DatabaseReference getMyChats() {
        return getUserData("chats");
    }

    public DatabaseReference getMyProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return getRoot().child(user.getUid());
    }

}
