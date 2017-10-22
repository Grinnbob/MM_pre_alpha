package com.mycompany.grifon.mm_pre_alpha.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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


    private DatabaseReference getUserData(String path){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        return getRoot().child(user.getUid()).child(path);
    }
    public DatabaseReference getMySubscribers(){
        return getUserData("subscribers");
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
