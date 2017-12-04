package com.mycompany.mm_pre_alpha.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.mm_pre_alpha.engine.firebase.FirebasePathHelper;
import com.mycompany.mm_pre_alpha.data.PlainUser;
import com.mycompany.mm_pre_alpha.data.events.users.AllMyUsersEvent;
import com.mycompany.mm_pre_alpha.ui.subscribers.SubscribersAdapter;
import com.mycompany.mm_pre_alpha.engine.eventbus.EBActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.ListIterator;

public class AllUsersActivity extends EBActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private RecyclerView rvSubscribers;
    private SubscribersAdapter subscribersAdapter;
    private EditText et_searchUsers;
    private List<PlainUser> myPlainUsers;
    private String searchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribers);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvSubscribers = (RecyclerView) findViewById(R.id.rvSubscribers);

        createWall();

        et_searchUsers = (EditText) findViewById(R.id.et_search_users);
        findViewById(R.id.btn_search_subscribers).setOnClickListener(this);
    }

    private void createWall() {
        subscribersAdapter = new SubscribersAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvSubscribers.setLayoutManager(layoutManager);
        rvSubscribers.setAdapter(subscribersAdapter);
        FirebasePathHelper.getInstance().requestAllUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all_users, menu);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AllMyUsersEvent event) {
        myPlainUsers = event.getPlainUsers();
       // Падало потому что мы удаляли из листа - то у нас всё падало
        /* List<PlainUser> myPlainUsersCopy = Collections.singletonList(myPlainUsers);
        if (!(searchName == null || "".equals(searchName))) {

            for (PlainUser myPlainUser : myPlainUsers) {
                if (!myPlainUser.getName().toLowerCase().contains(searchName)) {
                    myPlainUsers.remove(myPlainUser);
                }
            }
        }*/
        if (!(searchName == null || "".equals(searchName))) {
            for (ListIterator<PlainUser> iter = myPlainUsers.listIterator(); iter.hasNext();) {
                PlainUser myPlainUser = iter.next();
                if (!myPlainUser.getName().toLowerCase().contains(searchName)) {
                    iter.remove();
                }
                // 1 - can call methods of element
                // 2 - can use iter.remove() to remove the current element from the list
                // 3 - can use iter.add(...) to insert a new element into the list
                //     between element and iter->next()
                // 4 - can use iter.set(...) to replace the current element

                // ...
            }
        }
        //subscribersAdapter.replaceData();
        subscribersAdapter.replaceData(myPlainUsers);
    }

    @Override
    public void onClick(View view) {
        // поиск людей
        if (view.getId() == R.id.btn_search_subscribers) {
            searchName = et_searchUsers.getText().toString().toLowerCase();
            FirebasePathHelper.getInstance().requestAllUsers();
        }
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
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
