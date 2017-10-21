package com.mycompany.grifon.mm_pre_alpha;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PersonalData {
    static final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference profileDirectory;
    final DatabaseReference subscribersDirectory;
    final DatabaseReference musicDirectory;
    final DatabaseReference myProfileDataDirectory;
    final DatabaseReference chatsDirectory;
                    //UID
    public PersonalData(String id) {
        profileDirectory = root.child(id);

        subscribersDirectory = profileDirectory.child("subscribers");
        musicDirectory = profileDirectory.child("music");
        myProfileDataDirectory = profileDirectory.child("myProfileData");
        this.chatsDirectory = profileDirectory.child("chats");
    }
}
