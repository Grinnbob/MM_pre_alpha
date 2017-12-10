package com.mycompany.mm_pre_alpha.ui.base;

import java.lang.ref.WeakReference;

/**
 * Created by Vlad on 10.12.2017.
 */

public interface MvpPresenter<V extends MvpView> {

    void attachView(V view);
    void detachView();

    boolean isViewAttached();
    V getView();
}
