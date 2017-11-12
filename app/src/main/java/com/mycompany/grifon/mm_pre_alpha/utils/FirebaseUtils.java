package com.mycompany.grifon.mm_pre_alpha.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mycompany.grifon.mm_pre_alpha.LoginActivity;
import com.mycompany.grifon.mm_pre_alpha.data.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.Post;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.Profile;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.SongInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUtils {

    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    public String myUuid;
    public Profile myProfile;

    public FirebaseUtils(){
        // Получаем доступ к Хранилищу storage
        storage = FirebaseStorage.getInstance();
        // Получаем доступ к Хранилищу database
        database = FirebaseDatabase.getInstance();
        // Создаем ссылки на руты
        storageRef = storage.getReference();
        databaseRef = database.getReference();

        // my uuid
        myUuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // my profile
        getMyProfile();
    }

    // загружает файл в Cloud Storage и его uri в Database
    public void uploadFileInFirebase(final Uri uri, final String name, final String postText) {
        // Создаем ссылку в Storage Firebase
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
        // устанавливаем 1ый слушатель прогресса (который почему-то не работает)
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
                writeSongInfoInDB(name, downloadUri.toString(), postText);
                //myRef.setValue(uri);
                //Toast.makeText(FirebaseUtils.this, "Композиция загружена", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // пишем музыку в Database
    private void writeSongInfoInDB(String name, String url, String postText) {
        SongInfo info = new SongInfo(name, url, 0);
        String key = databaseRef.push().getKey();
        databaseRef.child("music").child(key).setValue(info);
        Log.d("MyLog", "post text: " + postText);
        if(!postText.equals("none"))
            updatePostsProfile(info, postText);
    }

    // пишем post в Database in my profile
    private void updatePostsProfile(SongInfo info, String postText) {
        // add in profile the same
        Post post = new Post(postText, info);
        Log.d("MyLog", "my uuid: " + myUuid);
        Log.d("MyLog", "my name: " + myProfile.getName());
        List<Post> posts;
        List<Post> playList;
        if(myProfile.getPosts() != null) {
            posts = myProfile.getPosts();
        } else posts = new ArrayList<>();
        if(myProfile.getUserPlayList() != null) {
            playList = myProfile.getUserPlayList();
        } else playList = new ArrayList<>();
        playList.add(post);
        posts.add(post);
        Profile profile = new Profile(myProfile.getName(), myProfile.getUuid(), myProfile.getInformation(), myProfile.getSubscribers(), myProfile.getSubscriptions(), playList, posts);
        FirebasePathHelper.writeNewProfileDB(profile);
        //TODO: writeNewProfileDB -> writeNewPostDB by key
        //todo: add likes
    }

    /*
     1)плохо получать все данные в основном потоке
     2)Не очень хорошо, что список mDataSet один для всех вызовов  onDataChange
     Получается что listener держит полный dataset - это не очень правильно.
     Если я буду сидеть в этом Activity, а ты будет закачивать в это время музло,
     то всё будет добавляться в этот массив - будет утечка памяти,
     если так долго сидеть в этом активити то приложение упадёт из-за нехватки памяти.
     3)getMusicSet() может вернуть пустой set - тут может быть ситуация,
     что эта фунция вернёт пустой список,
     в которой загрузились ещё не все значения.
     Потому что onDataChange() вызывается не тогда когда мы прошлись по коду,
     а когда страницу обновили (обновление может случиться через несколько миллисекунд после того
     как вернулся этот список mDataSet)
     */
    // получаем список хранящейся в Database музыки
    public List<SongInfo> getMusicSet() {
        final List<SongInfo> mDataSet = new ArrayList<>();//не поддерживает многопоточность

        databaseRef.child("music").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("FB", "Current thread: " + Thread.currentThread().getName());
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
        Log.e("FB", "Music array size: " + mDataSet.size());
        return mDataSet;
    }

    // получаем список хранящиеся в Database посты текущего пользователя
    public List<Post> getMyPostSet() {
        final List<Post> mDataSet = new ArrayList<>();//не поддерживает многопоточность

        databaseRef.child("users").child(myUuid).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("FB", "Current thread: " + Thread.currentThread().getName());
                Post post;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    post = dsp.getValue(Post.class);
                    mDataSet.add(post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Log.e("FB", "Posts array size: " + mDataSet.size());
        return mDataSet;
    }

    /*
    Вроде тут можно не проводить фильтрацию на клиенте, а запросить фильтрованные данные.
     */

    // получаем список искомой (хранящейся в Database) музыки
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

    public void getMyProfile() {
        //return getRoot().child(String.valueOf(R.string.users_path)).child(user.getUid()).child(path);
        databaseRef.child("users").child(myUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myProfile = dataSnapshot.getValue(Profile.class);
                //event fired!
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
