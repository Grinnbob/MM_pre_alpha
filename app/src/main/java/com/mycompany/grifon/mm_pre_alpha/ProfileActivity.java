package com.mycompany.grifon.mm_pre_alpha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;//recyclerwiew
    private Intent intentSubscribers;
    private Intent intentNews;
    private Intent intentMusic;
    private Intent intentChat;
    private View chatView;
    private PlainUser plainUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.chatButton).setOnClickListener(this);

        /*findViewById(R.id.btn_add_music).setOnClickListener(this);
        findViewById(R.id.btn_play).setOnClickListener(this);*/

        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.tg_Mode);
        toggleButton.setOnCheckedChangeListener(toggleListener);
    }

    CompoundButton.OnCheckedChangeListener toggleListener =  new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                // отписка
            }else{
                // подписка
                //plainUser.
              /*  String key = mDatabase.child("users").push().getKey();
                Post post = new Post(userId, username, title, body);
                Map<String, Object> postValues = post.toMap();*/
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        plainUser = (PlainUser) getIntent().getSerializableExtra("user");
        // fill data
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.chatButton) {
            intentChat = new Intent(ProfileActivity.this, ChatActivity.class);
            startActivity(intentChat);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
            case R.id.subscribers:
                intentSubscribers = new Intent(this, SubscribersActivity.class);
                startActivity(intentSubscribers);
                this.finish();
                break;
            case R.id.music:
                intentMusic = new Intent(this, MusicActivity.class);
                startActivity(intentMusic);
                this.finish();
                break;
            case R.id.news:
                intentNews = new Intent(this, NewsActivity.class);
                startActivity(intentNews);
                this.finish();
                break;
            case R.id.profile:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
