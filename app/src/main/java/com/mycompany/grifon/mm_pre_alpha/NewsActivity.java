package com.mycompany.grifon.mm_pre_alpha;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mycompany.grifon.mm_pre_alpha.data.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.utils.FirebaseUtils;
import com.mycompany.grifon.mm_pre_alpha.utils.RecyclerViewAdapter;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.Post;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.Profile;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.SongInfo;

import java.util.List;

public class NewsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Intent intentSubscribers;
    private Intent intentMusic;
    private Intent intentProfile;

    private Post post;
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

        //create new post
        findViewById(R.id.btn_add_post).setOnClickListener(this);
        postText = (EditText) findViewById(R.id.et_postText);
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
        // добавить post
        if(view.getId() == R.id.btn_add_post) {
            // Выбираем файл на смартфоне и загружаем в Firebase storage and database
            Intent intent = new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, SELECT_MUSIC);
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
                firebaseUtils.uploadFileInFirebase(selectedAudioUri, name, postText.getText().toString());
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
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
            case R.id.subscribers:
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

    public static FirebaseUtils getFirebaseUtils(){return firebaseUtils;}
}


