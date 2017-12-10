package com.mycompany.mm_pre_alpha.ui.profile.user;

import com.mycompany.mm_pre_alpha.data.events.profile.UserProfileEvent;
import com.mycompany.mm_pre_alpha.ui.base.BasePresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Vlad on 10.12.2017.
 */

public class UserProfilePresenter extends BasePresenter<UserProfileContract.View>
    implements UserProfileContract.Presenter {

    /* Contract */
    // --------------------------------------------------------------------------------------------
    @Override
    public void openChatScreen() {
        // TODO
    }

    /* Event Bus */
    // --------------------------------------------------------------------------------------------
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserProfileData(UserProfileEvent evt) {
        if (isViewAttached()) getView().showUserName(evt.getProfile().getName());
    }
}
