package com.mycompany.mm_pre_alpha.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

/**
 * Created by Vlad on 10.12.2017.
 */

public abstract class BaseActivity<V extends MvpView, P extends MvpPresenter<V>>
        extends AppCompatActivity implements MvpView {

    private P presenter;

    protected abstract P createPresenter();

    protected P getPresenter() {
        return presenter;
    }

    @SuppressWarnings("unchecked") @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
        presenter.attachView((V) this);

        if (presenter instanceof BasePresenter) {
            ((BasePresenter<V>) presenter).busSubscribe();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (presenter instanceof BasePresenter) {
            ((BasePresenter<V>) presenter).busUnsubscribe();
        }

        presenter.detachView();
    }
}
