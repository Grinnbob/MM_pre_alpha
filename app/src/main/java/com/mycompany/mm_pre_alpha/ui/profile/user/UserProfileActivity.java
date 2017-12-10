package com.mycompany.mm_pre_alpha.ui.profile.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.mm_pre_alpha.ui.base.BaseActivity;

public class UserProfileActivity extends BaseActivity<UserProfileContract.View, UserProfileContract.Presenter>
    implements UserProfileContract.View {

    @Override
    protected UserProfileContract.Presenter createPresenter() {
        return new UserProfilePresenter();
    }

    /* Lifecycle */
    // --------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView();

        Button chatButton = (Button) findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().openChatScreen();
            }
        });
    }

    /* Contract */
    // --------------------------------------------------------------------------------------------
    @Override
    public void showUserName(String name) {
        // tv.setText(name);
    }
}
