package com.mycompany.mm_pre_alpha.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.mm_pre_alpha.engine.firebase.FirebasePathHelper;
import com.mycompany.mm_pre_alpha.data.PlainUser;
import com.mycompany.mm_pre_alpha.data.events.users.AllMyUsersEvent;
import com.mycompany.mm_pre_alpha.data.events.profile.MyProfileEvent;
import com.mycompany.mm_pre_alpha.data.events.profile.UserProfileEvent;
import com.mycompany.mm_pre_alpha.ui.subscribers.SubscribersAdapter;
import com.mycompany.mm_pre_alpha.engine.eventbus.EBActivity;
import com.mycompany.mm_pre_alpha.data.Profile;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubscriptionsActivity extends EBActivity {

    private Toolbar toolbar;

    RecyclerView rvSubscribers;
    SubscribersAdapter subscribersAdapter;

    private PlainUser plainUser;
    FirebaseUser user;
    Profile profile;//тот чел на которого ткнули чтобы посмотреть
    Profile myProfile;//наш профиль

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_subscribers);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            rvSubscribers = (RecyclerView) findViewById(R.id.rvSubscribers);
            subscribersAdapter = new SubscribersAdapter(this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rvSubscribers.setLayoutManager(layoutManager);
            rvSubscribers.setAdapter(subscribersAdapter);
            //FirebasePathHelper.requestAllUsers();

            user = FirebaseAuth.getInstance().getCurrentUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Intent intent = getIntent();
            plainUser = (PlainUser) intent.getSerializableExtra("user");
            if (plainUser == null) { //попали сюда не ткнув на какого-то пользователя, а ткнули со своего профиля
                plainUser = new PlainUser(user);
            }
            FirebasePathHelper.getInstance().getMyProfile(user.getUid());
            FirebasePathHelper.getInstance().getUserProfile(plainUser.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMyProfileData(MyProfileEvent evt) {
        try {
            //   plainUser = new PlainUser(user.getDisplayName(), user.getUid());
            myProfile = evt.getProfile();
            if (myProfile != null && myProfile.getUuid().equals(plainUser.getUuid())) {
                Map<String, PlainUser> subscriptions = myProfile.getSubscriptions();
                subscribersAdapter.replaceData(new ArrayList<>(subscriptions.values()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserProfileData(UserProfileEvent evt) {
        try {
            if (plainUser == null) {
                plainUser = new PlainUser(user);
            }
            profile = evt.getProfile();
            if (profile != null && profile.getUuid().equals(plainUser.getUuid())) {
                Map<String, PlainUser> subscriptions = profile.getSubscriptions();
                subscribersAdapter.replaceData(new ArrayList<>(subscriptions.values()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subscribers, menu);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AllMyUsersEvent event) {
        List<PlainUser> myPlainUsers = event.getPlainUsers();
        subscribersAdapter.replaceData(myPlainUsers);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
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
                Intent intentAllUsers = new Intent(this, AllUsersActivity.class);
                startActivity(intentAllUsers);
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
