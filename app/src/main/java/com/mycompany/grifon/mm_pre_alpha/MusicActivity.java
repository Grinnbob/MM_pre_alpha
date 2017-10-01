package com.mycompany.grifon.mm_pre_alpha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Intent intentSubscribers;
    private Intent intentNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btn_add_music).setOnClickListener(this);
        findViewById(R.id.btn_play).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // добавить музыку
        if(view.getId() == R.id.btn_add_music) {
            // Получаем доступ к Хранилищу
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Создаем ссылку на рут
            StorageReference storageRef = storage.getReference();
            // Создаем ссылку на файл
            StorageReference mountainsRef = storageRef.child("audio/mountains.jpg");


            // воспроизвести музыку
        } else if(view.getId() == R.id.btn_play) {

        }

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
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}

