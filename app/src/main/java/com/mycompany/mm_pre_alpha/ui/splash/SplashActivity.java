package com.mycompany.mm_pre_alpha.ui.splash;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.mm_pre_alpha.ui.NewsActivity;

// активити загрузки
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //todo: do all downloadings in this activity
        /*
        // подключаемся к Firebase
        firebaseUtils = new FirebaseUtils();
        // получаем полный список, хранящихся в БД песен
        final List<Post> myDataset = firebaseUtils.getPostSet(false);
        Log.d("MY LOG:", "POSTS SET: " + myDataset);
        if(myDataset.isEmpty()) {
            Log.d("MY LOG:", "POSTS SET is empty ");
            //Post emptyPost = new Post("none", new SongInfo("none", "none", 0));
            //myDataset.add(emptyPost);
        }
*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent newsIntent = new Intent(SplashActivity.this, NewsActivity.class);
               // newsIntent.putExtra("data", myDataset);
                startActivity(newsIntent);
                finish();
            }
        }, 1*500);

    }
}
