package com.mycompany.grifon.mm_pre_alpha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mycompany.grifon.mm_pre_alpha.data.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.events.AllMyUsersEvent;
import com.mycompany.grifon.mm_pre_alpha.ui.subscribers.SubscribersAdapter;
import com.mycompany.grifon.mm_pre_alpha.utils.EBActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class AllUsersActivity extends EBActivity {

    private Toolbar toolbar;

    RecyclerView rvSubscribers;
    SubscribersAdapter subscribersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribers);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvSubscribers = (RecyclerView) findViewById(R.id.rvSubscribers);
        subscribersAdapter= new SubscribersAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        rvSubscribers.setLayoutManager(layoutManager);
        rvSubscribers.setAdapter(subscribersAdapter);
        FirebasePathHelper.requestAllUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all_users, menu);
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AllMyUsersEvent event) {
        List<PlainUser> myPlainUsers = event.getPlainUsers();
        subscribersAdapter.replaceData(myPlainUsers);
    };




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
            case R.id.subscribers:
                Intent intentSubscribers = new Intent(this, SubscribersActivity.class);
                startActivity(intentSubscribers);
                this.finish();
                break;
            case R.id.music:
                Intent intentMusic = new Intent(this, MusicActivity.class);
                startActivity(intentMusic);
                this.finish();
                break;
            case R.id.news:
                Intent intentNews = new Intent(this, NewsActivity.class);
                startActivity(intentNews);
                this.finish();
                break;
            case R.id.profile:
                Intent intentProfile = new Intent(this, ProfileActivity.class);
                startActivity(intentProfile);
                this.finish();
                break;
            case R.id.all_users:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
