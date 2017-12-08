package com.mycompany.grifon.mm_pre_alpha.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.data.Post;
import com.mycompany.grifon.mm_pre_alpha.data.Profile;
import com.mycompany.grifon.mm_pre_alpha.data.SongInfo;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebasePathHelper;

import java.util.UUID;

public class AddSongToPostActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText postText;
    private TextView tv_songName;
    private String myUuid;
    private SongInfo songInfo;
    private PlainUser plainUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_add_song_to_post);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            myUuid = mAuth.getCurrentUser().getUid();
            getMyProfile();

            Intent intent = getIntent();
            String songName = intent.getStringExtra("song_name");
            String songUrl = intent.getStringExtra("song_url");
            //String songLikes = intent.getStringExtra("song_likes");

            //todo: add likes
            songInfo = new SongInfo(songName, songUrl, 0);

            tv_songName = (TextView) findViewById(R.id.tv_song_name);
            tv_songName.setText(songName);
            tv_songName.setOnClickListener(this);

            //create new post
            findViewById(R.id.btn_add_post_2).setOnClickListener(this);
            postText = (EditText) findViewById(R.id.et_postText_2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        // добавить post
        if (view.getId() == R.id.btn_add_post_2) {
            try {
                String time = String.valueOf(System.currentTimeMillis());
                Post post = new Post(postText.getText().toString(), songInfo, plainUser, time, UUID.randomUUID().toString());

                FirebasePathHelper.getInstance().writeNewPostDB(myUuid, post);
                // пишем post в Database in subscribers profiles
                FirebasePathHelper.getInstance().writeNewPostToSubscribersDB(myUuid, post);
                Toast.makeText(AddSongToPostActivity.this, "Post Added", Toast.LENGTH_SHORT).show();
                this.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getMyProfile() {
        try {
            DatabaseReference databaseRef;
            FirebaseDatabase database;
            database = FirebaseDatabase.getInstance();
            databaseRef = database.getReference();
            databaseRef.child("users").child(myUuid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Profile myProfile = dataSnapshot.getValue(Profile.class);
                    plainUser = new PlainUser(myProfile.getName(), myUuid);
                    Log.d("MyLog", "my name: " + myProfile.getName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
