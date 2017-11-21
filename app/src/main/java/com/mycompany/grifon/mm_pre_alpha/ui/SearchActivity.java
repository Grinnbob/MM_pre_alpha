package com.mycompany.grifon.mm_pre_alpha.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.ui.music.RecyclerViewAdapterMusic;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebaseUtils;
import com.mycompany.grifon.mm_pre_alpha.data.SongInfo;

import java.util.List;

// search music
public class SearchActivity extends AppCompatActivity{

    // для стены
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapterMusic mAdapter;

    // song name to write in database and storage
    private FirebaseUtils firebaseUtils;

    // song name for search
    private EditText searchName;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // подключаемся к Firebase
        firebaseUtils = NewsActivity.getFirebaseUtils();
        // получаем полный список, хранящихся в БД песен
        Intent intent = getIntent();
        String songName = intent.getStringExtra("data");
        //Log.d(LOG_TAG, searchName.getText().toString() + "!!!!!!!!");
        List<SongInfo> myDataset = firebaseUtils.getSearchedDataSet(songName);
        // создаём стену
        createWall(myDataset);
    }

    // создаём стену
    private void createWall(List<SongInfo> myDataset) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerViewAdapterMusic(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }
}
