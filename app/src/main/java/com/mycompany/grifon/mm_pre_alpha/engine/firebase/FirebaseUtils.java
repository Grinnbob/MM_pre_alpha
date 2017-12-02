package com.mycompany.grifon.mm_pre_alpha.engine.firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;

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
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.data.Post;
import com.mycompany.grifon.mm_pre_alpha.data.Profile;
import com.mycompany.grifon.mm_pre_alpha.data.SongInfo;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class FirebaseUtils {

    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    public String myUuid;
    public Profile myProfile =  null;
    public PlainUser plainUser;

    public FirebaseUtils() {
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
        //or?
        //Profile profile = FirebaseAuthHelper.getInstance().getProfile();
        Log.d("MyLog", "my uuid: " + myUuid);
    }

    // загружает файл в Cloud Storage и его uri в Database
    public void uploadFileInFirebase(final Uri uri, final String name, final String postText, final ProgressBar progressBar) {
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
                int currentprogress = (int) progress;
                progressBar.setProgress(currentprogress);
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
        // "none" ключевое слово, означает, что только добавляем музыку в бд без создания поста
        if (!postText.equals("none")) {
            Post post;
            //Log.d("myLog!!!", "time formed: " + currentTime);
            try {
                post = new Post(postText, info, plainUser, System.currentTimeMillis(), UUID.randomUUID().toString());
                // пишем post в Database in my profile
                FirebasePathHelper.getInstance().writeNewPostDB(myUuid, post);


                // пишем post в Database in subscribers profiles
                FirebasePathHelper.getInstance().writeNewPostToSubscribersDB(myUuid, post);

            } catch (Exception e) {
                Log.d("myLog!!!", "post didn't added in firebase: " + e);
            }
        }
    }

    /*
     1)плохо получать все данные в основном потоке
     2)Не очень хорошо, что список mDataSet один для всех вызовов  onDataChange
     Получается что listener держит полный dataset - это не очень правильно.
     Если я буду сидеть в этом Activity, а ты будет закачивать в это время музло,
     то всё будет добавляться в этот массив - будет утечка памяти,
     если так долго сидеть в этом активити то приложение упадёт из-за нехватки памяти.
     3)getDataSet() может вернуть пустой set - тут может быть ситуация,
     что эта фунция вернёт пустой список,
     в которой загрузились ещё не все значения.
     Потому что onDataChange() вызывается не тогда когда мы прошлись по коду,
     а когда страницу обновили (обновление может случиться через несколько миллисекунд после того
     как вернулся этот список mDataSet)
     */
    // получаем список хранящейся в Database музыки
    public List<SongInfo> getDataSet() {
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

    // получаем список хранящиеся в Database посты
    // true - мои посты, false - все
    public LinkedHashMap<String, Post> getPostSet(String uuid, boolean postType) {
        final LinkedHashMap<String,Post> mDataSet = new LinkedHashMap<>();//не поддерживает многопоточность
        if (!postType) {
            // all posts
            databaseRef.child("users").child(uuid).child("posts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("FB", "Current thread: " + Thread.currentThread().getName());
                    Post post;
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        post = dsp.getValue(Post.class);
                        mDataSet.put(post.getUuid(),post);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            // my posts
            databaseRef.child("users").child(uuid).child("posts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("FB", "Current thread: " + Thread.currentThread().getName());
                    //PlainUser me = FirebaseAuthHelper.getInstance().getProfile().toPlain();
                    //getMyProfile();
                    if (myProfile!=null) { //подпора
                        PlainUser me = myProfile.toPlain();
                        Post post;
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            post = dsp.getValue(Post.class);
                            if (me.equals(post.getAuthor()))
                                mDataSet.put(post.getUuid(),post);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        Log.e("FB", "Posts array size: " + mDataSet.size());
        // sorting by timestamps
        /*Collections.sort(mDataSet, new Comparator<Post>() {
            public int compare(Post post1, Post post2) {
                return post1.toString().compareTo(post2.toString());
            }
        });*/
        return mDataSet;
    }

    // добавляем существующие посты новому подписчику в ленту
    public void addPostToSubscribersDB(final PlainUser subscribtionPlainUser) {
        // all posts
        databaseRef.child("users").child(subscribtionPlainUser.getUuid()).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("FB", "Current thread: " + Thread.currentThread().getName());
                Post post;
                PlainUser me = FirebaseAuthHelper.getInstance().getProfile().toPlain();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    post = dsp.getValue(Post.class);
                    if (me.equals(post.getAuthor())) {
                        Post subPost = new Post(post.getText(), post.getSong(), subscribtionPlainUser, post.getTimestamp(),UUID.randomUUID().toString());
                        FirebasePathHelper.getInstance().writeNewPostDB(myUuid, subPost);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // удаляем посты из стены при отписке
    public void deleteSubscribersPostsDB(final PlainUser subscribtionPlainUser) {
      //  final String authorUuid = subscribtionPlainUser.getUuid();
        // all posts
        databaseRef.child("users").child(myUuid).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("FB", "Current thread: " + Thread.currentThread().getName());
                Post post;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    post = dsp.getValue(Post.class);
                    if (post.getAuthor() != null) {
                        if (post.getAuthor().equals(subscribtionPlainUser))
                            dsp.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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

                    if (songInfo.getName().toLowerCase().contains(searchedName.toLowerCase()))
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
        databaseRef.child("users").child(myUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myProfile = dataSnapshot.getValue(Profile.class);
                plainUser = new PlainUser(myProfile.getName(), myUuid);
                Log.d("MyLog", "my name: " + myProfile.getName());
                //event fired!
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // проверяем, подписаны ли мы на этого чела, чтобы поставить галочку
    private boolean result = false;
    public boolean isMySubscribtion(final String uuid) {

        databaseRef.child("users").child(myUuid).child("subscriptions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PlainUser plainUser;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    plainUser = dsp.getValue(PlainUser.class);
                    if (plainUser.getUuid().equals(uuid))
                        result = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return result;
    }

    // глобальные лайки, доделать
    // переделать концепцию, сейчас просматриваются ВСЕ песни, чтобы понять в какую поставили лайк
    // сделать сеттер для лайков?
    /*
    private String likes = "0";
    public String getSongLikes(final String url) {
        databaseRef.child("music").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SongInfo songInfo;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    songInfo = dsp.getValue(SongInfo.class);
                    if(songInfo.getUrl().equals(url)) {
                        likes = songInfo.getLikes();
                    }
                }
                Log.d("MyLog", "song likes: " + likes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return likes;
    }
    */

    // удаляем посты из своей стены
    public void deletePostDB(final Long postTimestamp) {
        databaseRef.child(myUuid).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("FB", "Current thread: " + Thread.currentThread().getName());
                Post post;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    post = dsp.getValue(Post.class);
                    if (post.getTimestamp()==postTimestamp) {
                        dsp.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
/*
        // удаляем посты из стен подписчиков
        databaseRef.child(myUuid).child("subscribers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("FB", "Current thread: " + Thread.currentThread().getName());
                PlainUser plainUser;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    plainUser = dsp.getValue(PlainUser.class);
                    delSubPostDB(plainUser.getUuid(), postTimestamp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/
    }

    public void delSubPostDB(String userUuid, final long postTimestamp){
        //удаляем посты из стен подписчиков
        databaseRef.child("users").child(userUuid).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("FB", "Current thread: " + Thread.currentThread().getName());
                Post post;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    post = dsp.getValue(Post.class);
                    if (post.getTimestamp()==postTimestamp) {
                        dsp.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
