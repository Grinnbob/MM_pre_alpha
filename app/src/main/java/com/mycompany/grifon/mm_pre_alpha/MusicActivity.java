package com.mycompany.grifon.mm_pre_alpha;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mycompany.grifon.mm_pre_alpha.utils.RecyclerViewAdapter;
import com.mycompany.grifon.mm_pre_alpha.utils.FirebaseUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Intent intentSubscribers;
    private Intent intentNews;
    private Intent intentProfile;

    private static final int SELECT_MUSIC = 1;
    private String selectedAudioPath;

    // для стены
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String name = null;
    public FirebaseUtils firebaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // подключаемся к Firebase
        firebaseUtils = new FirebaseUtils();
        // создаём стену
        createWall();

        findViewById(R.id.btn_add_music).setOnClickListener(this);
        findViewById(R.id.btn_play).setOnClickListener(this);
    }

    // создаём стену
    private void createWall() {
        List<String> myDataset = firebaseUtils.getDataSet();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // если мы уверены, что изменения в контенте не изменят размер layout-а RecyclerView
        // передаем параметр true - это увеличивает производительность
        mRecyclerView.setHasFixedSize(true);

        // используем linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // создаем адаптер
        mAdapter = new RecyclerViewAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        // добавить музыку
        if(view.getId() == R.id.btn_add_music) {
            // Выбираем файл на смартфоне и загружаем в Firebase storage and database
            Intent intent = new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, SELECT_MUSIC);

            // воспроизвести музыку ... хз что это делает, доделать
        } else if(view.getId() == R.id.btn_play) {

            if (android.os.Build.VERSION.SDK_INT >= 15) {
                Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN,
                        Intent.CATEGORY_APP_MUSIC);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Min SDK 15
                startActivity(intent);
            } else {
                Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER); //"android.intent.action.MUSIC_PLAYER"
                startActivity(intent);
            }
        }
    }

    // выбирает файл
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_MUSIC)
            {
                Uri selectedAudioUri = data.getData();
                selectedAudioPath = getPath(selectedAudioUri);

                // загружаем в бд музыку
                firebaseUtils.uploadFileInFirebase(selectedAudioUri, name);
            }
        }
    }

    // получить абсолютный путь к выбранному файлу по uri
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Audio.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();

        // get name of file
        Cursor returnCursor =
                getContentResolver().query(uri, null, null, null, null);
        int name_index = returnCursor
                .getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        name = returnCursor.getString(name_index);
        return cursor.getString(column_index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music, menu);
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

