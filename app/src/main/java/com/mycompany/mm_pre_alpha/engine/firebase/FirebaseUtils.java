package com.mycompany.mm_pre_alpha.engine.firebase;

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
import com.mycompany.mm_pre_alpha.data.PlainUser;
import com.mycompany.mm_pre_alpha.data.Post;
import com.mycompany.mm_pre_alpha.data.Profile;
import com.mycompany.mm_pre_alpha.data.SongInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class FirebaseUtils {

    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    public String myUuid;
    public Profile myProfile = null;
    public PlainUser plainUser;

    public FirebaseUtils() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // загружает файл в Cloud Storage и его uri в Database
    public void uploadFileInFirebase(final Uri uri, final String name, final String postText, final ProgressBar progressBar) {
        try {
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
                    // write in DB
                    try {
                        writeSongInfoInDB(name, downloadUri.toString(), postText);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // пишем музыку в Database
    private void writeSongInfoInDB(String name, String url, String postText) throws Exception {
        SongInfo info = new SongInfo(name, url, 0);
        String key = databaseRef.push().getKey();
        databaseRef.child("music").child(key).setValue(info);
        Log.d("MyLog", "post text: " + postText);
        // "none" ключевое слово, означает, что только добавляем музыку в бд без создания поста
        if (!postText.equals("none")) {
            Post post;
            //Log.d("myLog!!!", "time formed: " + currentTime);
            try {
                String time = String.valueOf(System.currentTimeMillis());
                post = new Post(postText, info, plainUser, time, UUID.randomUUID().toString());

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
    public List<SongInfo> getDataSet() throws Exception {
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
    public LinkedHashMap<String, Post> getPostSet(final String uuid, boolean postType) throws Exception {
        final LinkedHashMap<String, Post> mDataSet = new LinkedHashMap<>();//не поддерживает многопоточность
        if (!postType) {
            // all posts
            databaseRef.child("users").child(uuid).child("posts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("FB", "Current thread: " + Thread.currentThread().getName());
                    Post post;
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        post = dsp.getValue(Post.class);
                        mDataSet.put(post.getUuid(), post);
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
                    if (myProfile != null) { //подпора
                        Post post;
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            post = dsp.getValue(Post.class);
                            if (uuid.equals(post.getAuthor().getUuid()))
                                mDataSet.put(post.getUuid(), post);
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

    // добавляем существующие посты новому подписчику (себе) в ленту
    public void addSubPostsToMeDB(final PlainUser subscribtionPlainUser) throws Exception {
        final String subUuid = subscribtionPlainUser.getUuid();
        // all posts
        databaseRef.child("users").child(subUuid).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("FB", "Current thread: " + Thread.currentThread().getName());
                Post post;
                Post subPost;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    post = dsp.getValue(Post.class);
                    if (subUuid.equals(post.getAuthor().getUuid())) {
                        subPost = new Post(post.getText(), post.getSong(), subscribtionPlainUser, post.getTimestamp(), UUID.randomUUID().toString());
                        FirebasePathHelper.getInstance().writeNewPostDB(myUuid, subPost);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // удаляем посты из своей стены при отписке
    public void deleteSubscribersPostsDB(final PlainUser subscribtionPlainUser) throws Exception {
        final String authorUuid = subscribtionPlainUser.getUuid();
        // all posts
        databaseRef.child("users").child(myUuid).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*Log.e("FB", "Current thread: " + Thread.currentThread().getName());
                Post post;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    post = dsp.getValue(Post.class);
                    if (post.getAuthor() != null) {
                        if (authorUuid.equals(post.getAuthor().getUuid()))
                            dsp.getRef().removeValue();
                    }
                }*/
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
    public List<SongInfo> getSearchedDataSet(final String searchedName) throws Exception {
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

    public void getMyProfile() throws Exception {
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

    public boolean isMySubscribtion(final String uuid) throws Exception {

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
}
