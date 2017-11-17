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
import com.mycompany.grifon.mm_pre_alpha.data.events.users.AllMyUsersEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.profile.UserProfileEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.profile.MyProfileEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.profile.SubscribersEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.chat.ChangeMessageEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.chat.NewMessageEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.chat.RemoveMessageEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.chat.WholeChatEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebasePathHelper {

    private static final String TAG = "!!!!!myLog????";
    private static volatile DatabaseReference root = null;

    public static DatabaseReference getRoot(){
        if(root == null)
        synchronized (FirebasePathHelper.class){
            if(root == null){
                DatabaseReference local = FirebaseDatabase.getInstance().getReference();
                root = local;
            }
        }
        return root;
    }

    // upload profile in Database (alternative)
    public static void uploadProfileDB(Profile info) {
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("users/" + info.getUuid(), info.toMap());
        getRoot().updateChildren(childUpdates);
    }

    private static DatabaseReference getUserData(String path) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return getRoot().child("users").child(user.getUid()).child(path);
    }

    public static void requestSubscribers(String uuid){
        //getRoot().child("users").child(uuid).addValueEventListener(new ValueEventListener() {
            getRoot().child("users").child(uuid).child("subscribers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, PlainUser> plainUsers = new HashMap<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
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

    public static void requestAllUsers() {
        getRoot().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PlainUser> plainUsers = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
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

    public static void getMyProfile(String uuid) {
        //return getRoot().child(String.valueOf(R.string.users_path)).child(user.getUid()).child(path);
        getRoot().child("users").child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);
                //event fired!
                EventBus.getDefault().post(new MyProfileEvent(profile));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getUserProfile(String uuid) {
        //return getRoot().child(String.valueOf(R.string.users_path)).child(user.getUid()).child(path);
        getRoot().child("users").child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);
                //event fired!
                EventBus.getDefault().post(new UserProfileEvent(profile));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static void getChat(String uuid) {
        //return getRoot().child(String.valueOf(R.string.users_path)).child(user.getUid()).child(path);
        getRoot().child("chats").child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chat chat=dataSnapshot.getValue(Chat.class);
                //event fired!
                EventBus.getDefault().post(new WholeChatEvent(chat));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static DatabaseReference getChatMessagesReference(String uuid) {
        return getRoot().child("chats").child(uuid).child("messages");
    }

    public static void subscribeToNewMessages(String uuid) {
        //return getRoot().child(String.valueOf(R.string.users_path)).child(user.getUid()).child(path);
        getRoot().child("chats").child(uuid).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message msg=dataSnapshot.getValue(Message.class);
                //event fired!
                EventBus.getDefault().post(new NewMessageEvent(msg));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Message msg=dataSnapshot.getValue(Message.class);
                //event fired!
                EventBus.getDefault().post(new ChangeMessageEvent(msg));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Message msg=dataSnapshot.getValue(Message.class);
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
    public static void writeNewProfileDB(Profile info) {
        getRoot().child("users").child(info.getUuid()).setValue(info);
    }

    // пишем new post в Database in my profile
    public static void writeNewPostDB(String uuid, Post post) {
        String key = getRoot().push().getKey();
        getRoot().child("users").child(uuid).child("posts").child(key).setValue(post);
    }

    // пишем new profile в Database
    public static void createChat(Profile first, PlainUser second, Chat chat) {

        getRoot().child("users").child(first.getUuid()).child("chats").child(chat.getUuid()).setValue(new PlainChat(chat));
        getRoot().child("users").child(second.getUuid()).child("chats").child(chat.getUuid()).setValue(new PlainChat(chat));
        getRoot().child("chats").child(chat.getUuid()).setValue(chat);
    }

    public static void updateChat(Chat chat) {
        //public Profile(String name, String uuid, String information, List<Profile> subscribers, List<Profile> subscriptions, List<SongInfo> userPlayList, List<Post> posts) {
        //  Profile newProfile = new Profile(info.getName(), info.getUuid(), info.getInformation(), subscribers, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        getRoot().child("chats").child(chat.getUuid()).setValue(chat);
    }
    public static void addMessageToChat(String chatUUID,Message message) {
        getRoot().child("chats").child(chatUUID).child("messages").child(message.getUuid()).setValue(message);
    }

    public DatabaseReference getMyMusic(){
        return getUserData("music");
    }
    public DatabaseReference getMyChats(){
        return getUserData("chats");
    }
    public DatabaseReference getMyProfile(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        return getRoot().child(user.getUid());
    }
}
