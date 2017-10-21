package com.mycompany.grifon.mm_pre_alpha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;//recyclerwiew
    private Intent intentSubscribers;
    private Intent intentNews;
    private Intent intentProfile;
    private Intent intentChat;
    private View chatView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.chatButton).setOnClickListener(this);

        /*findViewById(R.id.btn_add_music).setOnClickListener(this);
        findViewById(R.id.btn_play).setOnClickListener(this);*/
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
            case R.id.subscriptions:
                intentSubscribers = new Intent(this, SubscribersActivity.class);
                startActivity(intentSubscribers);
                this.finish();
                break;
            case R.id.music:
                break;
            case R.id.news:
                intentNews = new Intent(this, NewsActivity.class);
                startActivity(intentNews);
                this.finish();
                break;
            case R.id.profile:
                intentProfile = new Intent(this, ProfileActivity.class);
                startActivity(intentProfile);
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
