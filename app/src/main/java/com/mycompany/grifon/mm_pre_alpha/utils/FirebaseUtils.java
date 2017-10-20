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
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Iterator;
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
        // metadata (likes)
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("likes", "0")
                .build();
        // создаем uploadTask посредством вызова метода putFile(), в качестве аргумента идет созданная нами ранее Uri
        UploadTask uploadTask = myRef.putFile(uri, metadata);
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
                writeNewInfoToDB(name, downloadUri.toString(), 0);
                //myRef.setValue(uri);
                //Toast.makeText(FirebaseUtils.this, "Композиция загружена", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void writeNewInfoToDB(String name, String url, int likes) {
        UploadInfo info = new UploadInfo(name, url, likes);

        String key = databaseRef.push().getKey();
        databaseRef.child("music").child(key).setValue(info);
    }

    // получаем список хранящейся в Database музыки
    public List<String> getDataSet() {
        final List<String> mDataSet = new ArrayList<>();
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        StorageReference storageRef = storage.getReference();
        // Get reference to the files
        StorageReference ref = storageRef.child("music");

        databaseReference.child("music").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    String audio = iter.next().getValue(String.class);
                    mDataSet.add(audio);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return mDataSet;
    }
}
