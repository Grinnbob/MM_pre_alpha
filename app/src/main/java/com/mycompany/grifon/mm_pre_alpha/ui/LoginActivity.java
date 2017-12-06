package com.mycompany.grifon.mm_pre_alpha.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.events.login.LoginEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.login.RegistrationEvent;
import com.mycompany.grifon.mm_pre_alpha.engine.eventbus.EBActivity;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebaseAuthHelper;
import com.mycompany.grifon.mm_pre_alpha.engine.music.MediaPlayerService;
import com.mycompany.grifon.mm_pre_alpha.ui.splash.SplashActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends EBActivity implements View.OnClickListener {

    private EditText email;
    private EditText password;
    private EditText userName;

    // player
    public static MediaPlayerService player;
    public static boolean serviceBound = false;

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
        String em = email.getText().toString();
        String pass = password.getText().toString();
        String name = userName.getText().toString();
        if (view.getId() == R.id.btn_sign_in) {
            // временная заглушка
            if (TextUtils.isEmpty(em) || TextUtils.isEmpty(pass)) {
                //FirebaseAuthHelper.getInstance().signIn("best@yandex.ru", "123456", "");
                //FirebaseAuthHelper.getInstance().signIn("post@test.io", "123456", "");
                //FirebaseAuthHelper.getInstance().signIn("post2@test.io", "123456", "");
                FirebaseAuthHelper.getInstance().signIn("morge@yandex.ru", "123456", "");
            } else {
                FirebaseAuthHelper.getInstance().signIn(em, pass, "");
            }
        } else if (view.getId() == R.id.btn_registration) {
            if (TextUtils.isEmpty(name))
                Toast.makeText(LoginActivity.this, "Empty Name", Toast.LENGTH_SHORT).show();
            else if(TextUtils.isEmpty(em))
                Toast.makeText(LoginActivity.this, "Empty Email", Toast.LENGTH_SHORT).show();
            else if(TextUtils.isEmpty(pass))
                Toast.makeText(LoginActivity.this, "Empty Password", Toast.LENGTH_SHORT).show();
            else FirebaseAuthHelper.getInstance().registration(em, pass, name);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignIn(LoginEvent event) {
        if (event.isSucceed()) {

            Toast.makeText(LoginActivity.this, R.string.login_authorisation_success_toast_message, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
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

    //Binding this Client to the AudioPlayer Service
    public static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            //Toast.makeText(context, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
}

