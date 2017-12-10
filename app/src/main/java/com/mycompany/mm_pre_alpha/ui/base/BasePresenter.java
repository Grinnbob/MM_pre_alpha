package com.mycompany.mm_pre_alpha.ui.base;

import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

/**
 * Created by Vlad on 10.12.2017.
 */

public abstract class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    WeakReference<V> viewRef;

    @Override
    public void attachView(V view) {
        viewRef = new WeakReference<V>(view);
    }

    @Override
    public void detachView() {
        viewRef.clear();
        viewRef = null;
    }

    @Override
    public boolean isViewAttached() {
        return getView() != null;
    }

    @Nullable @Override
    public V getView() {
        if (viewRef == null) return null;
        return viewRef.get();
    }

    void busSubscribe() {
        EventBus.getDefault().register(this);
    }

    void busUnsubscribe() {
        EventBus.getDefault().unregister(this);
    }
}
