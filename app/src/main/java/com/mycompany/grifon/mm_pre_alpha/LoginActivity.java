package com.mycompany.grifon.mm_pre_alpha;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


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
        if(view.getId() == R.id.btn_sign_in) {
            //signIn(email.getText().toString(), password.getText().toString());
            signIn("1234@qwe.ru", "123456");
        } else if (view.getId() == R.id.btn_registration) {
            registration(email.getText().toString(), password.getText().toString());
        }

    }

    public void signIn(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.login_authorisation_success_toast_message, Toast.LENGTH_SHORT).show();
                    intent = new Intent(LoginActivity.this, NewsActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(LoginActivity.this, R.string.login_authorisation_failed_toast_message, Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void registration (String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.login_registration_success_toast_message, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(LoginActivity.this, R.string.login_registration_failed_toast_message, Toast.LENGTH_SHORT).show();
            }
        });
        //add user name
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // user root updated
                        }
                    }
                });
        intent = new Intent(LoginActivity.this, NewsActivity.class);
        startActivity(intent);
    }
}

