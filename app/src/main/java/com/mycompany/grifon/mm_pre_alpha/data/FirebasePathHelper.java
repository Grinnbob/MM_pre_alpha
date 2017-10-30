package com.mycompany.grifon.mm_pre_alpha.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mycompany.grifon.mm_pre_alpha.events.SubscribersEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class FirebasePathHelper {
    private static volatile DatabaseReference root=null;

    public static DatabaseReference getRoot(){
        if(root==null)
        synchronized (FirebasePathHelper.class){
            if(root==null){
                DatabaseReference local = FirebaseDatabase.getInstance().getReference();
                root = local;
            }
        }
        return root;
    }

    private void writeNewUser(String userId, String username, String title) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        PlainUser s = new PlainUser(username,userId);
        getRoot().child("users").child(userId).setValue(s);
        //Post post = new Post(userId, username, title, body);
        //Map<String, Object> postValues = post.toMap();

        //Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/posts/" + key, postValues);
        //childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        //mDatabase.updateChildren(childUpdates);
    }




    private static DatabaseReference getUserData(String path){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        return getRoot().child("users").child(user.getUid()).child(path);
    }
    public static void requestMySubscribers(){
        getUserData("subscribers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PlainUser> plainUsers = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                PlainUser plainUser = postSnapshot.getValue(PlainUser.class);
                    plainUsers.add(plainUser);
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

    public static void requestAllUsers(){
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
                EventBus.getDefault().post(new SubscribersEvent(plainUsers));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
    /*public DatabaseReference getChatsData(){
        getMyChats().
        return getRoot().child("chats").user.getUid()).child(path);
    }*/

}
