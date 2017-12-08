package com.mycompany.mm_pre_alpha.engine.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mycompany.mm_pre_alpha.data.Profile;
import com.mycompany.mm_pre_alpha.data.events.login.LoginEvent;
import com.mycompany.mm_pre_alpha.data.events.login.RegistrationEvent;
import com.mycompany.mm_pre_alpha.data.events.profile.MyProfileEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FirebaseAuthHelper {

    private volatile static FirebaseAuthHelper instance = null;

    private FirebaseAuth mAuth;
    private boolean signedIn = false;
    private final EventBus eventBus;

    public static boolean newUser = false;
    private Profile profile;
    /*public Profile myProfile;

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
    }*/


    public static FirebaseAuthHelper getInstance() {
        if (instance == null)
            synchronized (FirebasePathHelper.class) {
                if (instance == null) {
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
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    public void signIn(final String email, final String password, final String userName) {
        //signIn after registration
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try {
                    boolean success = task.isSuccessful() && (task.getResult().getUser() != null);
                    signedIn = success;
                    String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    //signIn after registration
                    //if there is no user in Database, we will add him
                    if (newUser) {
                        Profile myProfile = new Profile(userName, uuid);
                        //firebasePathHelper.uploadProfileDB(myProfile);
                        FirebasePathHelper.getInstance().uploadProfileDB(myProfile);
                    }
                    eventBus.post(new LoginEvent(success, email));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Profile getProfile() {
        return profile;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onProfileChanged(MyProfileEvent event) {
        this.profile = event.getProfile();
    }

    public void registration(final String email, final String password, final String userName) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try {
                    FirebaseUser user = null;
                    if (task.isSuccessful()) {
                        user = task.getResult().getUser();
                        if (user != null) {
                            newUser = true;
                            signIn(email, password, userName);
                            eventBus.post(new MyProfileEvent(profile));
                        }
                    }
                    eventBus.post(new RegistrationEvent(user != null, email));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}