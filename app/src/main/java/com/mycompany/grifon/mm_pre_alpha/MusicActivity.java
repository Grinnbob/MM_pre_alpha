package com.mycompany.grifon.mm_pre_alpha;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Intent intentSubscribers;
    private Intent intentNews;

    private static final int SELECT_MUSIC = 1;
    private String selectedAudioPath;
    private Uri uri;

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
            // Выбираем файл на смартфоне и загружаем в Firebase storage
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
    // хз как это работает, берём от сюда uri аудио-файла и передаём в бд для записи
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_MUSIC)
            {
                Uri selectedAudioUri = data.getData();
                selectedAudioPath = getPath(selectedAudioUri);
                // што эта такое????
                try {
                    FileInputStream files = new FileInputStream(selectedAudioPath);
                    BufferedInputStream bufferedStream = new BufferedInputStream(files);
                    byte[] bMapArray = new byte[bufferedStream.available()];
                    bufferedStream.read(bMapArray);
                    Bitmap bMap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
                    //Here you can set this /Bitmap image to the button background image

                    if (files != null) {
                        files.close();
                    }if (bufferedStream != null) {
                        bufferedStream.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // загружаем в бд музыку
                uploadFileInFirebaseStorage();
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
        return cursor.getString(column_index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music, menu);

        return true;
    }

    private void uploadFileInFirebaseStorage() {
        // Получаем доступ к Хранилищу
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Создаем ссылку на рут
        StorageReference storageRef = storage.getReference();
        // Создаем ссылку на файл
        StorageReference audiosRef = storageRef.child("audio/");

        // Создаем ссылку в Хранилище Firebase
        StorageReference riversRef = storageRef.child("audio/" + uri.getLastPathSegment());
        // создаем uploadTask посредством вызова метода putFile(), в качестве аргумента идет созданная нами ранее Uri
        UploadTask uploadTask = riversRef.putFile(uri);
        // устанавливаем 1-й слушатель на uploadTask, который среагирует, если произойдет ошибка, а также 2-й слушатель, который сработает в случае успеха операции
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Ошибка
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Успешно! Берем ссылку прямую https-ссылку на файл
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
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

