package com.mycompany.grifon.mm_pre_alpha;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StreamDownloadTask;
import com.mycompany.grifon.mm_pre_alpha.data.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.Post;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.Profile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText email;
    private EditText password;
    private EditText userName;

    private Intent intent;

    public static Profile myProfile;
    public static String uuid;
    public static boolean newUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //user.getUid() -- key
                if (user != null) {
                    // User is signed in
                    uuid = mAuth.getCurrentUser().getUid();
                } else {
                    // User is signed out

                }

            }
        };

        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);
        userName = (EditText) findViewById(R.id.et_userName);

        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_registration).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_sign_in) {
            //signIn(email.getText().toString(), password.getText().toString());

            signIn("post@test.io", "123456");
            //signIn("best@yandex.ru", "123456");

        } else if (view.getId() == R.id.btn_registration) {
            if (registration(email.getText().toString(), password.getText().toString()))
                signIn(email.getText().toString(), password.getText().toString());
        }
    }

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    uuid = mAuth.getCurrentUser().getUid();
                    //signIn after registration
                    //if there is no user in Database, we will add him
                    if (newUser) {
                        FirebasePathHelper firebasePathHelper = new FirebasePathHelper();
                        Map<String, PlainUser> subscribers = new HashMap<>();
                        Map<String, PlainUser> subscriptions = new HashMap<>();
                        //List<Post> userPlayList = Collections.emptyList();
                        Map<String, Post> posts = new HashMap<>();
                        myProfile = new Profile(userName.getText().toString(), uuid, "Add information!", subscribers, subscriptions, posts);
                        //firebasePathHelper.uploadProfileDB(myProfile);
                        firebasePathHelper.writeNewProfileDB(myProfile);
                    }
                    Toast.makeText(LoginActivity.this, R.string.login_authorisation_success_toast_message, Toast.LENGTH_SHORT).show();

                    intent = new Intent(LoginActivity.this, NewsActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(LoginActivity.this, R.string.login_authorisation_failed_toast_message, Toast.LENGTH_SHORT).show();

            }
        });
    }

    public boolean registration(String email, String password) {

        if (!userName.getText().toString().equals("")) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful() && !userName.getText().toString().equals("")) {
                        newUser = true;
                        Toast.makeText(LoginActivity.this, R.string.login_registration_success_toast_message, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(LoginActivity.this, R.string.login_registration_failed_toast_message, Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else {
            Toast.makeText(LoginActivity.this, "Введиде никнейм", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}

