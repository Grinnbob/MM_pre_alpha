package com.mycompany.grifon.mm_pre_alpha.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.events.login.LoginEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.login.RegistrationEvent;
import com.mycompany.grifon.mm_pre_alpha.engine.eventbus.EBActivity;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebaseAuthHelper;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.data.Profile;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends EBActivity implements View.OnClickListener {

   /* private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;*/

    private EditText email;
    private EditText password;
    private EditText userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);
        userName = (EditText) findViewById(R.id.et_userName);

        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_registration).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String em=email.getText().toString();
        String pass =password.getText().toString();
        if (view.getId() == R.id.btn_sign_in) {
            if(TextUtils.isEmpty(em)||TextUtils.isEmpty(pass)) {
                //FirebaseAuthHelper.getInstance().signIn("best@yandex.ru", "123456");
                FirebaseAuthHelper.getInstance().signIn("morge@yandex.ru", "123456");
            }else{
                FirebaseAuthHelper.getInstance().signIn(em,pass);
            }
        } else if (view.getId() == R.id.btn_registration) {
            FirebaseAuthHelper.getInstance().registration(em,pass);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignIn(LoginEvent event) {
        if(event.isSucceed()) {
            Toast.makeText(LoginActivity.this, R.string.login_authorisation_success_toast_message, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, NewsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(LoginActivity.this, R.string.login_authorisation_failed_toast_message, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegister(RegistrationEvent event) {
        if (event.isSucceed()) {
            Toast.makeText(LoginActivity.this, R.string.login_registration_success_toast_message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoginActivity.this, R.string.login_registration_failed_toast_message, Toast.LENGTH_SHORT).show();
        }
    }
}

