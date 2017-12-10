package com.mycompany.mm_pre_alpha.ui.profile.user;

import com.mycompany.mm_pre_alpha.ui.base.MvpPresenter;
import com.mycompany.mm_pre_alpha.ui.base.MvpView;

/**
 * Created by Vlad on 10.12.2017.
 */

public interface UserProfileContract {
    interface View extends MvpView {
        void showUserName(String name);
    }

    interface Presenter extends MvpPresenter<View> {
        void openChatScreen();
    }
}
