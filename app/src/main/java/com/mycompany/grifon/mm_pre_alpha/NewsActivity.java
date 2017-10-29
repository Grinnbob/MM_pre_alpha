package com.mycompany.grifon.mm_pre_alpha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mycompany.grifon.mm_pre_alpha.utils.FirebaseUtils;
import com.mycompany.grifon.mm_pre_alpha.utils.RecyclerViewAdapter;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.SongInfo;

import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Intent intentSubscribers;
    private Intent intentMusic;
    private Intent intentProfile;

    private static FirebaseUtils firebaseUtils;

    // для стены
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // подключаемся к Firebase
        firebaseUtils = new FirebaseUtils();
        // получаем полный список, хранящихся в БД песен
        List<SongInfo> myDataset = firebaseUtils.getDataSet();
        // создаём стену
        createWall(myDataset);
    }

    // создаём стену
    private void createWall(List<SongInfo> myDataset) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerViewAdapter(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
            case R.id.subscriptions:
                intentSubscribers = new Intent(this, SubscribersActivity.class);
                startActivity(intentSubscribers);
                break;
            case R.id.music:
                intentMusic = new Intent(this, MusicActivity.class);
                startActivity(intentMusic);
                this.finish();
                break;
            case R.id.news:
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

    public static FirebaseUtils getFirebaseUtils(){return firebaseUtils;}
}


