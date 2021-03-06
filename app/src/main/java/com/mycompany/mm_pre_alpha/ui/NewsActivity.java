package com.mycompany.mm_pre_alpha.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.mm_pre_alpha.engine.firebase.FirebaseUtils;
import com.mycompany.mm_pre_alpha.ui.music.RecyclerViewAdapterPosts;
import com.mycompany.mm_pre_alpha.data.Post;

import java.util.LinkedHashMap;

public class NewsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private Intent intentMusic;
    private Intent intentProfile;

    private ProgressBar progressBar;
    private EditText postText;
    // song name to write in database and storage
    private String name = null;
    private static FirebaseUtils firebaseUtils;

    private static final int SELECT_MUSIC = 1;
    // не удалять!! не будет нихрена работать
    private String selectedAudioPath;
    private Uri selectedAudioUri;

    // для стены
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapterPosts mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        //for splash
        //Intent intent = getIntent();
        //List<Post> dataSet = intent.getStringExtra("data");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        try {
            // подключаемся к Firebase
            firebaseUtils = new FirebaseUtils();
            // получаем полный список всех постов
            final String myUuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("MY LOG:", "my uuid: " + myUuid);
            // all users posts
            // тут передаём все посты данного пользователя ( те где он является автором(т.е. те которые он запостил или репостнул) + все остальные
            final LinkedHashMap<String, Post> myDataset = firebaseUtils.getPostSet(myUuid, false);
            //final Map<String, Post> myDataset =
            if (myDataset.isEmpty())
                Log.d("MY LOG:", "POSTS SET is empty ");
            // создаём стену
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createWall(myDataset, myUuid);
                }
            }, 1 * 500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create new post
        findViewById(R.id.btn_add_post).setOnClickListener(this);
        postText = (EditText) findViewById(R.id.et_postText);
    }

    // создаём стену
    private void createWall(LinkedHashMap<String, Post> myDataset, String myUuid) {
        try {
            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            mAdapter = new RecyclerViewAdapterPosts(this, myDataset, myUuid, false, false, firebaseUtils);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        // добавить post
        if (view.getId() == R.id.btn_add_post) {
            // Выбираем файл на смартфоне и загружаем в Firebase storage and database
            try {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, SELECT_MUSIC);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // выбирает файл
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_MUSIC) {
                try {
                    selectedAudioUri = data.getData();
                    selectedAudioPath = getPath(selectedAudioUri);

                    // загружаем в бд музыку
                    // и создаём свой пост
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    firebaseUtils.uploadFileInFirebase(selectedAudioUri, name, postText.getText().toString(), progressBar);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // получить абсолютный путь к выбранному файлу по uri и имя файла
    public String getPath(Uri uri) throws Exception {
        String[] projection = {MediaStore.Audio.Media.DATA};
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
        // отрезаем .mp3
        if (name.contains(".mp3")) {
            name = name.replaceAll(".mp3", "");
        } else if (name.contains(".wma")) {
            name = name.replaceAll(".wma", "");
        }
        return cursor.getString(column_index);
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
            case R.id.music:
                intentMusic = new Intent(this, MusicActivity.class);
                startActivity(intentMusic);
                this.finish();
                break;
            case R.id.news:
                Intent intentNews = new Intent(this, NewsActivity.class);
                startActivity(intentNews);
                this.finish();
                break;
            case R.id.profile:
                intentProfile = new Intent(this, ProfileActivity.class);
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

    public static FirebaseUtils getFirebaseUtils() {
        return firebaseUtils;
    }
}


