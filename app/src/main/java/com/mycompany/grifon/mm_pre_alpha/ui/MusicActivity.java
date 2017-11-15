package com.mycompany.grifon.mm_pre_alpha.ui;

import android.content.Intent;
import android.database.Cursor;
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
import java.util.List;
import android.widget.EditText;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.ui.music.RecyclerViewAdapter;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebaseUtils;
import com.mycompany.grifon.mm_pre_alpha.data.SongInfo;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private Intent intentSubscribers;
    private Intent intentNews;
    private Intent intentProfile;
    private Intent searchActivity;

    private static final int SELECT_MUSIC = 1;
    // не удалять!! не будет нихрена работать
    private String selectedAudioPath;
    private Uri selectedAudioUri;

    // для стены
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;

    // song name to write in database and storage
    private String name = null;
    private FirebaseUtils firebaseUtils;
    private FirebasePathHelper firebasePathHelper;

    // song name for search
    private static EditText et_searchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // подключаемся к Firebase
        firebaseUtils = NewsActivity.getFirebaseUtils();
        firebasePathHelper = new FirebasePathHelper();
        // получаем полный список, хранящихся в БД песен
        List<SongInfo> myDataset = firebaseUtils.getDataSet();
        // создаём стену
        createWall(myDataset);

        et_searchName = (EditText) findViewById(R.id.et_search);
        findViewById(R.id.btn_search_music).setOnClickListener(this);
        findViewById(R.id.btn_add_music).setOnClickListener(this);
    }

    // создаём стену
    private void createWall(List<SongInfo> myDataset) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerViewAdapter(this, myDataset);
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

            // search music
        } else if(view.getId() == R.id.btn_search_music) {
            searchActivity =  new Intent(this, SearchActivity.class);
            startActivity(searchActivity);
        }
    }

    // выбирает файл
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_MUSIC)
            {
                selectedAudioUri = data.getData();
                selectedAudioPath = getPath(selectedAudioUri);

                // загружаем в бд музыку
                firebaseUtils.uploadFileInFirebase(selectedAudioUri, name);
            }
        }
    }

    // получить абсолютный путь к выбранному файлу по uri и имя файла
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
            case R.id.subscribers:
                Intent intentSubscribers = new Intent(this, SubscribersActivity.class);
                startActivity(intentSubscribers);
                this.finish();
                break;
            case R.id.music:
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

    // передаёт в поиск название песни
    public static EditText getSearchedSongName() {return et_searchName;}
}

