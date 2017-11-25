package com.mycompany.grifon.mm_pre_alpha.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.PlainChat;
import com.mycompany.grifon.mm_pre_alpha.data.Post;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.data.events.profile.UserProfileEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.profile.MyProfileEvent;
import com.mycompany.grifon.mm_pre_alpha.engine.eventbus.EBActivity;
import com.mycompany.grifon.mm_pre_alpha.data.Profile;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebaseUtils;
import com.mycompany.grifon.mm_pre_alpha.ui.music.RecyclerViewAdapterPosts;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class ProfileActivity extends EBActivity implements View.OnClickListener {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private Toolbar toolbar;//recyclerwiew
    private Intent intentSubscribers;
    private Intent intentNews;
    private Intent intentMusic;
    private Intent intentChat;
    private View chatView;
    private PlainUser plainUser;//либо я, либо тот чел на которого ткнули чтобы посмотреть
    private TextView tv_userName;
    private TextView tv_numberOfSubscribers;
    private TextView tv_numberOfSubscriptions;
    private TextView tv_subscribeMe;
    private TextView tv_numberOfSameSongs;
    private TextView tv_sameSongs;
    private CheckBox checkBox;
    FirebaseUser user;
    Profile profile;//тот чел на которого ткнули чтобы посмотреть
    Profile myProfile;//наш профиль
    private Button chatButton;

    private static FirebaseUtils firebaseUtils;

    // для стены
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapterPosts mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chatButton = (Button) findViewById(R.id.chatButton);
        chatButton.setOnClickListener(this);
        checkBox = (CheckBox) findViewById(R.id.subscribeCheckBox);
        checkBox.setOnCheckedChangeListener(checkBoxListener);

        tv_userName = (TextView) findViewById(R.id.tv_userName);
        tv_numberOfSubscribers = (TextView) findViewById(R.id.tv_numberOfSubscribers);
        tv_numberOfSubscribers.setOnClickListener(this);
        Log.i(TAG, "All is up!");
        tv_numberOfSubscriptions = (TextView) findViewById(R.id.tv_numberOfSubscriptions);
        tv_numberOfSubscriptions.setOnClickListener(this);
        //tv_numberOfPublications.setOnClickListener(this);
        tv_subscribeMe = (TextView) findViewById(R.id.tv_subscribe_me);
        tv_subscribeMe.setOnClickListener(this);

        tv_numberOfSameSongs = (TextView) findViewById(R.id.tv_number_of_the_same_songs);
        tv_numberOfSameSongs.setOnClickListener(this);
        tv_sameSongs = (TextView) findViewById(R.id.tv_same_songs);
        tv_sameSongs.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);


       /*Intent intent = getIntent();
        plainUser = (PlainUser) intent.getSerializableExtra("user");
        if (plainUser == null) { //попали сюда не ткнув на какого-то пользователя, а ткнули на свой профиль просто
            plainUser = new PlainUser(user);
        }

        FirebasePathHelper.getMyProfile(user.getUid());
        FirebasePathHelper.getUserProfile(plainUser.getUuid());*/

    }

    // создаём стену
    private void createWall(List<Post> myDataset, String myUuid, boolean profyleType) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new RecyclerViewAdapterPosts(this, myDataset, myUuid, profyleType, true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) {
                // отписка
                if (profile.getSubscribers().size() > 0) {
                    profile.getSubscribers().remove(user.getUid());
                    FirebasePathHelper.writeNewProfileDB(profile);
                }

                if (myProfile.getSubscriptions().size() > 0) {
                    myProfile.getSubscriptions().remove(plainUser.getUuid());
                    FirebasePathHelper.writeNewProfileDB(myProfile);
                }

                // delete posts from wall
                firebaseUtils.deletePostToSubscribersDB(plainUser);

            } else {
                // подписка
                Map<String, PlainUser> subscribers = profile.getSubscribers();
                if (subscribers == null) {
                    subscribers = new HashMap<>();
                }
                subscribers.put(user.getUid(), new PlainUser(myProfile.getName(), user.getUid()));
                FirebasePathHelper.writeNewProfileDB(profile);

                Map<String, PlainUser> subscriptions = myProfile.getSubscriptions();
                subscriptions.put(plainUser.getUuid(), plainUser);
                FirebasePathHelper.writeNewProfileDB(myProfile);

                // add all posts to current user
                firebaseUtils.addPostToSubscribersDB(plainUser);
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMyProfileData(MyProfileEvent evt) {
        //   plainUser = new PlainUser(user.getDisplayName(), user.getUid());
        myProfile = evt.getProfile();
        if (myProfile != null && myProfile.getUuid().equals(plainUser.getUuid())) {
            tv_userName.setText(myProfile.getName());
            tv_numberOfSubscribers.setText(String.valueOf(myProfile.getSubscribers().size()));
            tv_numberOfSubscriptions.setText(String.valueOf(myProfile.getSubscriptions().size()));
            //tv_numberOfPublications.setText(String.valueOf(myProfile.getMyPostsSize()));
            setControls();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserProfileData(UserProfileEvent evt) {
        if (plainUser == null) {
            plainUser = new PlainUser(user);
        }
        profile = evt.getProfile();
        if (profile != null && profile.getUuid().equals(plainUser.getUuid())) {
            tv_userName.setText(profile.getName());
            tv_numberOfSubscribers.setText(String.valueOf(profile.getSubscribers().size()));
            tv_numberOfSubscriptions.setText(String.valueOf(profile.getSubscriptions().size()));
            //tv_numberOfPublications.setText(String.valueOf(myProfile.getMyPostsSize()));
            setControls();
        }

    }

    void setControls() {
        boolean isMine = myProfile != null && profile != null && myProfile.getUuid().equals(profile.getUuid());
        if (isMine) {
            //chatView.setVisibility(View.INVISIBLE);//later

            checkBox.setVisibility(View.INVISIBLE);
            chatButton.setVisibility(View.INVISIBLE);
            tv_subscribeMe.setVisibility(View.INVISIBLE);
            tv_numberOfSameSongs.setVisibility(View.INVISIBLE);
            tv_sameSongs.setVisibility(View.INVISIBLE);

        } else {
            //chatView.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.VISIBLE);
            tv_subscribeMe.setVisibility(View.VISIBLE);
            tv_numberOfSameSongs.setVisibility(View.VISIBLE);
            tv_sameSongs.setVisibility(View.VISIBLE);
        }
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribersData(SubscribersEvent evt) {
        if (plainUser == null) {
            plainUser = new PlainUser(user.getDisplayName(), user.getUid());
        }
        subscribers = evt.getSubscribers();
        if (subscribers == null) {
            subscribers = new HashMap<>();
        }
        //profile.setSubscribers(evt.getSubscribers());
      *//*  if (profile != null) {
            tv_subscribers.setText(profile.getSubscribers().size());
        }*//*
    }*/


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        plainUser = (PlainUser) intent.getSerializableExtra("user");
        if (plainUser == null) { //попали сюда не ткнув на какого-то пользователя, а ткнули на свой профиль просто
            plainUser = new PlainUser(user);
        }
        FirebasePathHelper.getMyProfile(user.getUid());
        FirebasePathHelper.getUserProfile(plainUser.getUuid());

        try {
            // подключаемся к Firebase
            firebaseUtils = new FirebaseUtils();
            // получаем полный список своих постов
            final String numberOfSameSongs;
            String currentUuid;
            final boolean profileType;
            boolean isMine = user != null && plainUser != null && user.getUid().equals(plainUser.getUuid());
            if (isMine) {
                currentUuid = user.getUid();
                profileType = true;

                numberOfSameSongs = "";
            } else {
                currentUuid = plainUser.getUuid();
                profileType = false;

                // не работает как надо - ничего не делает (должна стоять галочка, если подписан на этого чела)
                /*if(firebaseUtils.isMySubscribtion(currentUuid))
                    checkBox.setChecked(true);*/

                // считаем совпадения по песням
                numberOfSameSongs = getNumberOfTheSameSongs(user.getUid(), currentUuid);
            }

            // my posts
            final List<Post> currentDataSet = firebaseUtils.getPostSet(currentUuid, true);
            if (currentDataSet.isEmpty())
                Log.d("MY LOG:", "POSTS SET is empty ");


            // создаём стену
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createWall(currentDataSet, user.getUid(), profileType);
                    tv_numberOfSameSongs.setText(numberOfSameSongs);
                }
            }, 1 * 500);


        } catch (NullPointerException e) {
            Log.d("MY LOG:", "NPE: " + e);
        }
    }

    // совпадения по песням
    private String getNumberOfTheSameSongs(String myUid, String userUid) {
        Set<String> mySet = firebaseUtils.getSongSet(myUid);
        Set<String> userSet = firebaseUtils.getSongSet(userUid);
        Set<String> resSet = new HashSet<>();
        int a = mySet.size();
        Log.d("MY LOG:", "a: " + a);
        int b = userSet.size();
        Log.d("MY LOG:", "b: " + b);
        if (a == 0)
            return "0";
        else if (b == 0)
            return "0";
        else {
            resSet.addAll(mySet);
            resSet.addAll(userSet);
            int c = resSet.size();
            c = 1 - (c - b) / a;
            Log.d("MY LOG:", "c: " + c);
            return String.valueOf(c);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.chatButton) {
            Intent intentChat = new Intent(ProfileActivity.this, ChatActivity.class);
            PlainChat pc;
            pc = myProfile.getPlainChatWithUser(plainUser);
            if (pc == null) {
                pc = new PlainChat(myProfile.getName() + " with " + plainUser.getName(), UUID.randomUUID().toString(), Arrays.asList(myProfile.toPlain(), plainUser));
                FirebasePathHelper.createChat(pc);
            }
            intentChat.putExtra("chat", (Serializable) pc);
            startActivity(intentChat);
        }
        if (view.getId() == R.id.tv_numberOfSubscribers) {
            Intent intentSubscribers = new Intent(ProfileActivity.this, SubscribersActivity.class);
            intentSubscribers.putExtra("user", plainUser);
            startActivity(intentSubscribers);
        }
        if (view.getId() == R.id.tv_numberOfSubscriptions) {
            Intent intentSubscriptions = new Intent(ProfileActivity.this, SubscriptionsActivity.class);
            //Intent i = new Intent(c, ProfileActivity.class);
            //intentSubscribers.putExtra("user", plainUser);
            intentSubscriptions.putExtra("user", plainUser);
            //c.startActivity(i);
            startActivity(intentSubscriptions);
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
            case R.id.all_users:
                Intent intentAllUsers = new Intent(this, AllUsersActivity.class);
                startActivity(intentAllUsers);
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
