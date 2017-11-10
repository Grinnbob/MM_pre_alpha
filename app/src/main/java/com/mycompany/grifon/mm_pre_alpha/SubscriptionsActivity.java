package com.mycompany.grifon.mm_pre_alpha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.events.Events;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class SubscriptionsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Intent intentMusic;
    private Intent intentNews;
    private Intent intentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subscribers, menu);
        return true;
    }


   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSubscriptionsEvent(Events.SubscribersEvent event) {
        List<PlainUser> myPlainUsers = event.getArg();
        //ListViewAdapter.updateData();
    };*/




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
            case R.id.subscribers:
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
