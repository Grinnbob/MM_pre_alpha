package com.mycompany.grifon.mm_pre_alpha.utils;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.SongInfo;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtils {

    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    public FirebaseUtils(){
        // Получаем доступ к Хранилищу storage
        storage = FirebaseStorage.getInstance();
        // Получаем доступ к Хранилищу database
        database = FirebaseDatabase.getInstance();
        // Создаем ссылки на руты
        storageRef = storage.getReference();
        databaseRef = database.getReference();
    }

    // загружает файл в Cloud Storage и его uri в Database
    public void uploadFileInFirebase(final Uri uri, final String name) {
        // Создаем ссылку в Хранилище Firebase
        StorageReference myRef = storageRef.child("music").child(name);
        /*
        // metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("likes", "0")
                .build();

        // создаем uploadTask посредством вызова метода putFile(), в качестве аргумента идет созданная нами ранее Uri
        UploadTask uploadTask = myRef.putFile(uri, metadata);
        */
        UploadTask uploadTask = myRef.putFile(uri);
        // устанавливаем 1ый слушатель прогресса
        // 2-й слушатель на uploadTask, который среагирует, если произойдет ошибка,
        // а также 3-й слушатель, который сработает в случае успеха операции
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // считаем процетное соотношение загрузки и отображаем его пользователю
                @SuppressWarnings("VisibleForTests")
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload progress: " + progress + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Ошибка
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Успешно! Берем прямую https-ссылку на файл
                @SuppressWarnings("VisibleForTests")
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                // добавляем URI в database
                //DatabaseReference myRef = databaseRef.child("music").push();
                // write in DB
                writeNewInfoInDB(name, downloadUri.toString());
                //myRef.setValue(uri);
                //Toast.makeText(FirebaseUtils.this, "Композиция загружена", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // пишем в Database
    private void writeNewInfoInDB(String name, String url) {
        SongInfo info = new SongInfo(name, url, 0);
        String key = databaseRef.push().getKey();
        databaseRef.child("music").child(key).setValue(info);
    }

    // получаем список хранящейся в Database музыки
    public List<SongInfo> getDataSet() {
        final List<SongInfo> mDataSet = new ArrayList<>();

        databaseRef.child("music").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SongInfo songInfo;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    songInfo = dsp.getValue(SongInfo.class);
                    mDataSet.add(songInfo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return mDataSet;
    }

    // получаем список хранящейся в Database музыки
    public List<SongInfo> getSearchedDataSet(final String searchedName) {
        final List<SongInfo> mDataSet = new ArrayList<>();

        databaseRef.child("music").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SongInfo songInfo;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    songInfo = dsp.getValue(SongInfo.class);
                    if(songInfo.getName().toLowerCase().contains(searchedName.toLowerCase()))
                        mDataSet.add(songInfo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return mDataSet;
    }
}
