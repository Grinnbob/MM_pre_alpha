package com.mycompany.grifon.mm_pre_alpha.engine.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mycompany.grifon.mm_pre_alpha.data.Profile;
import com.mycompany.grifon.mm_pre_alpha.data.events.login.LoginEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.login.RegistrationEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.profile.MyProfileEvent;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Vlad on 15.11.2017.
 */

public class FirebaseAuthHelper {


    private volatile static FirebaseAuthHelper instance=null;


    private FirebaseAuth mAuth;
    private boolean signedIn=false;
    private final EventBus eventBus;


    private Profile profile;

    public static FirebaseAuthHelper getInstance(){
        if(instance == null)
            synchronized (FirebasePathHelper.class){
                if(instance == null){
                    FirebaseAuthHelper local = new FirebaseAuthHelper();
                    instance = local;
                }
            }
        return instance;
    }

    private FirebaseAuthHelper() {
        mAuth = FirebaseAuth.getInstance();
        // // TODO: 16.11.2017 fix usage
      /*  mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                signedIn=user!=null;
            }
        };*/
        eventBus=EventBus.getDefault();
        eventBus.register(this);
    }

    public boolean isSignedIn(){
        return signedIn;
    }
    public  void signIn(final String email,final String password){
        //signIn after registration
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                boolean success=task.isSuccessful() && (task.getResult().getUser() != null);
                signedIn=success;
                eventBus.post(new LoginEvent(success, email));

            }
        });
    }

    public Profile getProfile() {
        return profile;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfileChanged(MyProfileEvent event){
        this.profile=event.getProfile();
    }

    public void registration (final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user=null;
                if(task.isSuccessful()) {
                    user = task.getResult().getUser();
                    if(user!=null){
                       profile = new Profile(user.getEmail(), user.getUid(), "Add information!");
                       FirebasePathHelper.writeNewProfileDB(profile);
                       eventBus.post(new MyProfileEvent(profile));
                    }
                }
                eventBus.post(new RegistrationEvent(user!=null,email));
            }
        });
    }
}