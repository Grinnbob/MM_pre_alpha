package com.mycompany.grifon.mm_pre_alpha.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mycompany.grifon.mm_pre_alpha.events.AllMyUsersEvent;
import com.mycompany.grifon.mm_pre_alpha.events.UserProfileEvent;
import com.mycompany.grifon.mm_pre_alpha.events.MyProfileEvent;
import com.mycompany.grifon.mm_pre_alpha.events.SubscribersEvent;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.Profile;

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
    public void uploadProfileDB(Profile info) {
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

    // пишем new profile в Database
    public static void writeNewProfileDB(Profile info) {
        //public Profile(String name, String uuid, String information, List<Profile> subscribers, List<Profile> subscriptions, List<SongInfo> userPlayList, List<Post> posts) {
        //Profile newProfile = new Profile(info.getName(), info.getUuid(), info.getInformation(), subscribers, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        getRoot().child("users").child(info.getUuid()).setValue(info);
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
